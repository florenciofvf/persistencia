package br.com.persist.plugins.atributo;

import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.StringPool;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.atributo.aux.Anotacao;
import br.com.persist.plugins.atributo.aux.Arquivo;
import br.com.persist.plugins.atributo.aux.Campo;
import br.com.persist.plugins.atributo.aux.Classe;
import br.com.persist.plugins.atributo.aux.Comentario;
import br.com.persist.plugins.atributo.aux.Container;
import br.com.persist.plugins.atributo.aux.Espaco;
import br.com.persist.plugins.atributo.aux.Fragmento;
import br.com.persist.plugins.atributo.aux.Funcao;
import br.com.persist.plugins.atributo.aux.FuncaoInter;
import br.com.persist.plugins.atributo.aux.FuncaoJS;
import br.com.persist.plugins.atributo.aux.Import;
import br.com.persist.plugins.atributo.aux.Instrucao;
import br.com.persist.plugins.atributo.aux.Interface;
import br.com.persist.plugins.atributo.aux.Linha;
import br.com.persist.plugins.atributo.aux.MetodoGet;
import br.com.persist.plugins.atributo.aux.MetodoSet;
import br.com.persist.plugins.atributo.aux.Parametros;
import br.com.persist.plugins.atributo.aux.Return;
import br.com.persist.plugins.atributo.aux.ReturnJS;
import br.com.persist.plugins.atributo.aux.Tipo;
import br.com.persist.plugins.atributo.aux.Var;
import br.com.persist.plugins.atributo.aux.VarObjJS;

public class AtributoPagina extends Panel {
	public static final String APPLICATION_JSON = "{MediaType.APPLICATION_JSON}";
	public static final Import IMPORT_LIST = new Import("java.util.List");
	public static final Tipo SERVICE = new Tipo("Service", "service");
	public static final Tipo FILTER = new Tipo("Filter", "filter");
	public static final String PESQUISAR = "pesquisar";
	public static final String LIST_DTO = "List<DTO>";
	public static final String PUBLIC = "public";

	private static final long serialVersionUID = 1L;
	private final PainelAtributo painelAtributo;
	private final PainelFichario painelFichario;

	public AtributoPagina(File file) {
		painelAtributo = new PainelAtributo(file);
		painelFichario = new PainelFichario(this);
		montarLayout();
		abrir();
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelAtributo, painelFichario);
		SwingUtilities.invokeLater(() -> split.setDividerLocation(.33));
		add(BorderLayout.CENTER, split);
	}

	public String getConteudo() {
		return painelAtributo.getConteudo();
	}

	public String getNome() {
		return painelAtributo.getNome();
	}

	private void abrir() {
		painelAtributo.abrir();
	}

	public void excluir() {
		painelAtributo.excluir();
	}

	public void salvar(AtomicBoolean atomic) {
		painelAtributo.salvar(atomic);
	}

	public void setText(String conteudo) {
		painelAtributo.textArea.setText(conteudo);
	}

	public List<Atributo> getAtributos() {
		return painelAtributo.getAtributos();
	}

	class PainelAtributo extends Panel {
		private final JTable tabela = new JTable(new AtributoModelo());
		public final JTextPane textArea = new JTextPane();
		private static final long serialVersionUID = 1L;
		private final Toolbar toolbar = new Toolbar();
		private ScrollPane scrollPane;
		private final File file;

		public PainelAtributo(File file) {
			this.file = file;
			montarLayout();
			abrir();
		}

		private void montarLayout() {
			JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanelTextArea(), criarPanelTabela());
			SwingUtilities.invokeLater(() -> split.setDividerLocation(.5));
			add(BorderLayout.NORTH, toolbar);
			add(BorderLayout.CENTER, split);
		}

		private Panel criarPanelTextArea() {
			Panel panel = new Panel();
			Panel panelArea = new Panel();
			panelArea.add(BorderLayout.CENTER, textArea);
			scrollPane = new ScrollPane(panelArea);
			panel.add(BorderLayout.CENTER, scrollPane);
			return panel;
		}

		private Panel criarPanelTabela() {
			Panel panel = new Panel();
			panel.add(BorderLayout.CENTER, new ScrollPane(tabela));
			tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			return panel;
		}

		private int getValueScrollPane() {
			return scrollPane.getVerticalScrollBar().getValue();
		}

		private void setValueScrollPane(int value) {
			SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(value));
		}

		private class Toolbar extends BarraButton implements ActionListener {
			private Action tabelaAcao = acaoIcon("label.atualizar_tabela", Icones.SINCRONIZAR);
			private final TextField txtPesquisa = new TextField(15);
			private static final long serialVersionUID = 1L;
			private transient Selecao selecao;

			private Toolbar() {
				super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR);
				txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
				txtPesquisa.addActionListener(this);
				addButton(tabelaAcao);
				add(txtPesquisa);
				add(label);
				tabelaAcao.setActionListener(e -> carregar());
			}

			Action acaoIcon(String chave, Icon icon) {
				return Action.acaoIcon(AtributoMensagens.getString(chave), icon);
			}

			@Override
			protected void limpar() {
				try {
					StringWriter sw = new StringWriter();
					XMLUtil util = new XMLUtil(sw);
					util.prologo();
					util.abrirTag2("att");
					Atributo att = new Atributo();
					att.setNome("nome");
					att.setRotulo("Rotulo");
					att.setClasse("Classe");
					att.salvar(util);
					util.finalizarTag("att");
					util.close();
					textArea.setText(sw.toString());
				} catch (Exception ex) {
					Util.stackTraceAndMessage(AtributoConstantes.PAINEL_ATRIBUTO, ex, this);
				}
			}

			@Override
			protected void baixar() {
				abrir();
				selecao = null;
				label.limpar();
			}

			@Override
			protected void copiar() {
				String string = Util.getString(textArea);
				Util.setContentTransfered(string);
				copiarMensagem(string);
				textArea.requestFocus();
			}

			@Override
			protected void colar(boolean numeros, boolean letras) {
				Util.getContentTransfered(textArea, numeros, letras);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Util.isEmpty(txtPesquisa.getText())) {
					selecao = Util.getSelecao(textArea, selecao, txtPesquisa.getText());
					selecao.selecionar(label);
				} else {
					label.limpar();
				}
			}

			private void carregar() {
				try {
					AtributoHandler handler = new AtributoHandler();
					if (!Util.isEmpty(textArea.getText())) {
						XML.processar(new ByteArrayInputStream(textArea.getText().getBytes()), handler);
					}
					tabela.setModel(new AtributoModelo(handler.getAtributos()));
					Util.ajustar(tabela, getGraphics());
				} catch (Exception ex) {
					Util.stackTraceAndMessage(AtributoConstantes.PAINEL_ATRIBUTO, ex, this);
				}
				SwingUtilities.updateComponentTreeUI(this);
			}
		}

		private List<Atributo> getAtributos() {
			return ((AtributoModelo) tabela.getModel()).getLista();
		}

		private String getConteudo() {
			return textArea.getText();
		}

		private String getNome() {
			return file.getName();
		}

		private void abrir() {
			textArea.setText(Constantes.VAZIO);
			if (file.exists()) {
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
					StringBuilder sb = new StringBuilder();
					int value = getValueScrollPane();
					String linha = br.readLine();
					while (linha != null) {
						sb.append(linha + Constantes.QL);
						linha = br.readLine();
					}
					textArea.setText(sb.toString());
					setValueScrollPane(value);
				} catch (Exception ex) {
					Util.stackTraceAndMessage(AtributoConstantes.PAINEL_ATRIBUTO, ex, this);
				}
			}
		}

		private void excluir() {
			if (file.exists()) {
				Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
				try {
					Files.delete(path);
				} catch (IOException e) {
					Util.stackTraceAndMessage(AtributoConstantes.PAINEL_ATRIBUTO, e, this);
				}
			}
		}

		private void salvar(AtomicBoolean atomic) {
			if (!Util.confirmaSalvarMsg(this, Constantes.TRES,
					AtributoMensagens.getString("msg.confirmar_salvar_ativa"))) {
				return;
			}
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
				atomic.set(true);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(AtributoConstantes.PAINEL_ATRIBUTO, ex, this);
			}
		}
	}
}

class PainelFichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	PainelFichario(AtributoPagina pagina) {
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		addAba(new PainelDTO(pagina));
		addAba(new PainelFilter(pagina));
		addAba(new PainelView(pagina));
		addAba(new PainelJSController(pagina));
		addAba(new PainelJSService(pagina));
		addAba(new PainelRest(pagina));
		addAba(new PainelService(pagina));
		addAba(new PainelBean(pagina));
		addAba(new PainelDAO(pagina));
		addAba(new PainelDAOImpl(pagina));
		addAba(new PainelTest(pagina));
	}

	private void addAba(AbstratoPanel panel) {
		addTab(AtributoMensagens.getString(panel.getChaveTitulo()), panel);
	}
}

abstract class AbstratoPanel extends Panel {
	private static final long serialVersionUID = 1L;
	protected final JTextPane textArea = new JTextPane();
	private final Toolbar toolbar = new Toolbar();
	private final AtributoPagina pagina;

	AbstratoPanel(AtributoPagina pagina) {
		montarLayout();
		this.pagina = pagina;
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, criarPanelTextArea());
	}

	private Panel criarPanelTextArea() {
		Panel panel = new Panel();
		Panel panelArea = new Panel();
		panelArea.add(BorderLayout.CENTER, textArea);
		ScrollPane scrollPane = new ScrollPane(panelArea);
		panel.add(BorderLayout.CENTER, scrollPane);
		return panel;
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		private Toolbar() {
			super.ini(new Nil(), ATUALIZAR, COPIAR);
		}

		@Override
		protected void atualizar() {
			List<Atributo> lista = new ArrayList<>();
			for (Atributo att : pagina.getAtributos()) {
				if (!att.isIgnorar()) {
					lista.add(att);
				}
			}
			gerar(lista);
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}
	}

	protected void setText(String string) {
		textArea.setText(string);
	}

	abstract String getChaveTitulo();

	abstract void gerar(List<Atributo> atributos);
}

class PainelDTO extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelDTO(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.dto";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();
		Classe classe = new Classe("DTO");

		for (Atributo att : atributos) {
			Campo campo = new Campo(att.criarTipo());
			classe.add(campo);
		}

		for (Atributo att : atributos) {
			Tipo tipo = att.criarTipo();
			MetodoGet get = new MetodoGet(tipo);
			MetodoSet set = new MetodoSet(tipo);
			classe.add(new Linha());
			classe.add(get);
			classe.add(new Linha());
			classe.add(set);
		}

		classe.gerar(0, pool);
		setText(pool.toString());
	}
}

class PainelFilter extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelFilter(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.filter";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.add(new Import("javax.ws.rs.QueryParam"));
			arquivo.ql();
		}

		Classe classe = new Classe("Filter");
		arquivo.add(classe);

		int i = 0;
		for (Atributo att : atributos) {
			if (i++ > 0) {
				classe.add(new Linha());
			}
			Anotacao anotacao = new Anotacao("QueryParam", Util.citar2(att.getNome()), true);
			classe.add(anotacao);
			Campo campo = new Campo(att.criarTipo());
			classe.add(campo);
		}

		for (Atributo att : atributos) {
			Tipo tipo = att.criarTipo();
			MetodoGet get = new MetodoGet(tipo);
			MetodoSet set = new MetodoSet(tipo);
			classe.add(new Linha());
			classe.add(get);
			classe.add(new Linha());
			classe.add(set);
		}

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}
}

class PainelRest extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelRest(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.rest";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.add(AtributoPagina.IMPORT_LIST);
			arquivo.ql();
			arquivo.add(new Import("javax.inject.Inject"));
			arquivo.ql();
			arquivo.add(new Import("javax.ws.rs.Consumes"));
			arquivo.add(new Import("javax.ws.rs.Produces"));
			arquivo.ql();
			arquivo.add(new Import("javax.ws.rs.core.MediaType"));
			arquivo.ql();
			arquivo.add(new Import("javax.ws.rs.BeanParam"));
			arquivo.add(new Import("javax.ws.rs.GET"));
			arquivo.add(new Import("javax.ws.rs.Path"));
			arquivo.add(new Import("javax.ws.rs.Produces"));
			arquivo.add(new Import("javax.ws.rs.QueryParam"));
			arquivo.add(new Import("javax.ws.rs.core.MediaType"));
			arquivo.ql();
			arquivo.add(new Comentario("br.gov.dpf.framework.seguranca.RestSeguranca"));
			arquivo.ql();

			Anotacao path = new Anotacao("Path", Util.citar2("endPointRest"), true);
			arquivo.add(path);
		}

		Classe classe = new Classe("Rest extends ApplicationRest");
		arquivo.add(classe);

		injetar(classe, AtributoPagina.SERVICE);
		classe.ql();
		injetar(classe, new Tipo("ServicePDF", "servicePDF"));
		classe.ql();
		criarGetListaDTO(classe);
		classe.ql();
		criarGetGerarPDF(classe);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private void injetar(Classe classe, Tipo tipo) {
		Anotacao inject = new Anotacao("Inject", null, true);
		classe.add(inject);
		Campo service = new Campo(tipo);
		classe.add(service);
	}

	private void criarGetListaDTO(Classe classe) {
		classe.add(new Anotacao("GET", null, true));
		classe.add(new Anotacao("Path", Util.citar2("endPointMetodo"), true));
		classe.add(new Anotacao("Consumes", AtributoPagina.APPLICATION_JSON, true));
		classe.add(new Anotacao("Produces", AtributoPagina.APPLICATION_JSON, true));

		Parametros params = new Parametros(new Anotacao("BeanParam", null));
		params.add(new Espaco());
		params.add(AtributoPagina.FILTER);
		Funcao funcao = new Funcao(AtributoPagina.PUBLIC, AtributoPagina.LIST_DTO, AtributoPagina.PESQUISAR, params);
		funcao.add(new Return("", "service.pesquisar(filter)"));
		classe.add(funcao);
	}

	private void criarGetGerarPDF(Classe classe) {
		classe.add(new Anotacao("GET", null, true));
		classe.add(new Anotacao("Path", Util.citar2("endPointMetodo"), true));
		classe.add(new Anotacao("Consumes", AtributoPagina.APPLICATION_JSON, true));
		classe.add(new Anotacao("Produces", "{MediaType.APPLICATION_OCTET_STREAM}", true));

		Parametros params = new Parametros(new Anotacao("BeanParam", null));
		params.add(new Espaco());
		params.add(AtributoPagina.FILTER);
		Funcao funcao = new Funcao(AtributoPagina.PUBLIC, "Response", "gerarPDF", params);
		funcao.add(new Instrucao("DadosDTO dto = service.recuperarDTO(filter)"));
		funcao.add(new Instrucao("byte[] bytes = servicePDF.gerarPDF(dto)"));
		funcao.ql();
		funcao.add(new Instrucao("ResponseBuilder response = Response.ok(bytes)"));
		funcao.add(new Instrucao("response.header(\"Content-Disposition\", \"attachment;filename=arquivo.dpf\")"));
		funcao.add(new Instrucao("response.header(\"Content-type\", MediaType.APPLICATION_OCTET_STREAM)"));
		funcao.add(new Return("", "response.build()"));
		classe.add(funcao);
	}
}

class PainelService extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelService(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.service";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.add(AtributoPagina.IMPORT_LIST);
			arquivo.ql();
			arquivo.add(new Import("javax.ejb.Local"));
			arquivo.ql();

			Anotacao local = new Anotacao("Local", null, true);
			arquivo.add(local);
		}

		Interface interfac = new Interface("Service");
		arquivo.add(interfac);

		Parametros params = new Parametros(AtributoPagina.FILTER);
		FuncaoInter funcao = new FuncaoInter(AtributoPagina.LIST_DTO, AtributoPagina.PESQUISAR, params);
		interfac.add(funcao);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}
}

class PainelBean extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelBean(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.bean";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.add(AtributoPagina.IMPORT_LIST);
			arquivo.ql();
			arquivo.add(new Import("javax.ejb.LocalBean"));
			arquivo.add(new Import("javax.ejb.Stateless"));
			arquivo.add(new Import("javax.ejb.TransactionManagement"));
			arquivo.add(new Import("javax.ejb.TransactionManagementType"));
			arquivo.ql();

			Anotacao stateless = new Anotacao("Stateless", null, true);
			arquivo.add(stateless);
			Anotacao localBean = new Anotacao("LocalBean", null, true);
			arquivo.add(localBean);
			Anotacao transaction = new Anotacao("TransactionManagement", "TransactionManagementType.CONTAINER", true);
			arquivo.add(transaction);
		}

		Classe classe = new Classe("Bean implements Service");
		arquivo.add(classe);

		Anotacao inject = new Anotacao("Inject", null, true);
		classe.add(inject);
		Campo service = new Campo(new Tipo("DAO", "dao"));
		classe.add(service);
		classe.ql();

		Parametros params = new Parametros(AtributoPagina.FILTER);
		Funcao funcao = new Funcao(AtributoPagina.PUBLIC, AtributoPagina.LIST_DTO, AtributoPagina.PESQUISAR, params);
		funcao.add(new Return("", "dao.pesquisar(filter)"));
		classe.add(funcao);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}
}

class PainelDAO extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelDAO(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.dao";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.add(AtributoPagina.IMPORT_LIST);
			arquivo.ql();
		}

		Interface interfac = new Interface("DAO");
		arquivo.add(interfac);

		Parametros params = new Parametros(AtributoPagina.FILTER);
		FuncaoInter funcao = new FuncaoInter(AtributoPagina.LIST_DTO, AtributoPagina.PESQUISAR, params);
		interfac.add(funcao);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}
}

class PainelDAOImpl extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelDAOImpl(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.dao_impl";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.add(new Import("java.util.ArrayList"));
			arquivo.add(AtributoPagina.IMPORT_LIST);
			arquivo.ql();
			arquivo.add(new Import("javax.persistence.EntityManager"));
			arquivo.add(new Import("javax.persistence.PersistenceContext"));
		}

		Classe classe = new Classe("DAOImpl implements DAO");
		arquivo.add(classe);

		Anotacao context = new Anotacao("PersistenceContext", "unitName = " + Util.citar2("nomeUnit"), true);
		classe.add(context);
		Campo entityManager = new Campo(new Tipo("EntityManager", "entityManager"));
		classe.add(entityManager);
		classe.ql();

		Parametros params = new Parametros(AtributoPagina.FILTER);
		Funcao funcao = new Funcao(AtributoPagina.PUBLIC, AtributoPagina.LIST_DTO, AtributoPagina.PESQUISAR, params);
		funcao.add(new Instrucao("List<DTO> resp = new ArrayList<>()"));
		funcao.add(new Comentario("entityManager.find..."));
		funcao.ql();
		funcao.add(new Return("", "resp"));
		classe.add(funcao);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}
}

class PainelJSController extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelJSController(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.js_controller";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		arquivo.add(new Instrucao("Controller.$inject = ['$scope', '$state', 'NgTableParams', 'Service']"));

		String string = ", ";
		Parametros params = new Parametros();
		params.add(new Var("$scope"));
		params.append(string);
		params.add(new Var("$state"));
		params.append(string);
		params.add(new Var("NgTableParams"));
		params.append(string);
		params.add(new Var("Service"));
		FuncaoJS funcao = new FuncaoJS("function Controller", params);
		arquivo.add(funcao);

		funcao.add(new Instrucao("var vm = this"));
		funcao.ql();
		funcao.add(new Instrucao("vm.pesquisados = new NgTableParams()"));
		funcao.add(new Instrucao("vm.filtro = {}"));
		funcao.ql();

		funcao.add(fnParam(atributos));
//		pool.append(gerarFnValidar(atributos)).ql();
//		pool.append(gerarFnPesquisa()).ql();
//		pool.append(gerarFnPDF());

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private Container fnParam(List<Atributo> atributos) {
		FuncaoJS funcao = new FuncaoJS("function criarParam", new Parametros());
		funcao.add(objParam(atributos));
		funcao.ql();
		funcao.add(new Return("", "param"));
		return funcao;
	}

	private Container objParam(List<Atributo> atributos) {
		VarObjJS obj = new VarObjJS("param");
		for (int i = 0; i < atributos.size(); i++) {
			Atributo att = atributos.get(i);
			obj.add(new Fragmento(att.getNome() + ": vm.filtro." + att.getNome()));
			if (i + 1 < atributos.size()) {
				obj.append(",");
			}
			obj.ql();
		}
		return obj;
	}

	private static String gerarFnValidar(List<Atributo> atributos) {
		StringPool pool = new StringPool();
		pool.tab().append("function validarFiltro() {").ql();
		if (atributos.size() > 1) {
			pool.append(validarVazios(2, atributos));
			pool.ql();
		}
		for (int i = 0; i < atributos.size(); i++) {
			Atributo att = atributos.get(i);
			pool.append(validarObrigatorio(2, att));
			if (i + 1 < atributos.size()) {
				pool.ql();
			}
		}
		if (!atributos.isEmpty()) {
			pool.ql();
		}
		pool.tab(2).append("return null;").ql();
		pool.tab().append("}").ql();
		return pool.toString();
	}

	private static String validarVazios(int tab, List<Atributo> atributos) {
		if (atributos.isEmpty()) {
			return "";
		}
		StringPool pool = new StringPool();
		pool.tab(tab).append("if(" + vazios(atributos) + ") {").ql();
		pool.tab(tab + 1).append("return 'Favor preencher pelo ao menos um campo de pesquisa';").ql();
		pool.tab(tab).append("}").ql();
		return pool.toString();
	}

	private static String vazios(List<Atributo> atributos) {
		StringBuilder sb = new StringBuilder();
		for (Atributo att : atributos) {
			if (sb.length() > 0) {
				sb.append(" && ");
			}
			sb.append(att.gerarIsVazioJS());
		}
		return sb.toString();
	}

	private static String validarObrigatorio(int tab, Atributo att) {
		StringPool pool = new StringPool();
		pool.tab(tab).append("if(" + att.gerarIsVazioJS() + ") {").ql();
		pool.tab(tab + 1).append("return 'Campo " + att.getRotulo() + " Obrigat\u00F3rio.';").ql();
		pool.tab(tab).append("}").ql();
		return pool.toString();
	}

	private String gerarFnPesquisa() {
		StringPool pool = new StringPool();
		pool.tab().append("vm.pesquisar = function() {").ql();
		pool.tab(2).append("var msg = validarFiltro();").ql();
		pool.tab(2).append("if(isVazio(msg)) {").ql();
		pool.tab(3).append("Service.pesquisar(criarParam()).then(function(result) {").ql();
		pool.tab(4).append("var lista = result.data;").ql();
		pool.tab(4).append("vm.pesquisados.settings().dataset = lista;").ql();
		pool.tab(4).append("vm.pesquisados.reload();").ql();
		pool.tab(4).append("if(lista.length === 0) {").ql();
		pool.tab(5).append("Msg.info('Nenhum registro encontrado');").ql();
		pool.tab(5).append("$scope.$emit('msg', 'Nenhum registro encontrado', null, 'warning');").ql();
		pool.tab(4).append("}").ql();
		pool.tab(3).append("});").ql();
		pool.tab(2).append("} else {").ql();
		pool.tab(3).append("Msg.error(msg);").ql();
		pool.tab(3).append("$scope.$emit('msg', msg, null, 'warning');").ql();
		pool.tab(2).append("}").ql();
		pool.tab().append("};").ql();
		return pool.toString();
	}

	private String gerarFnPDF() {
		StringPool pool = new StringPool();
		pool.tab().append("vm.gerarPDF = function() {").ql();
		pool.tab(2).append("var msg = validarFiltro();").ql();
		pool.tab(2).append("if(isVazio(msg)) {").ql();
		pool.tab(3).append("Service.gerarPDF(criarParam()).then(function(result) {").ql();
		pool.tab(4).append("var file = new Blob([result.data], {type: 'application/pdf'});").ql();
		pool.tab(4).append("var downloadLink = angular.element('<a></a>');").ql();
		pool.tab(4).append("downloadLink.attr('href', window.URL.createObjectURL(file));").ql();
		pool.tab(4).append("downloadLink.attr('download', \"arquivo.pdf\");").ql();
		pool.tab(4).append("var link = downloadLink[0];").ql();
		pool.tab(4).append("document.body.appendChild(link);").ql();
		pool.tab(4).append("link.click();").ql();
		pool.tab(4).append("document.body.removeChild(link);").ql();
		pool.tab(3).append("});").ql();
		pool.tab(2).append("} else {").ql();
		pool.tab(3).append("Msg.error(msg);").ql();
		pool.tab(3).append("$scope.$emit('msg', msg, null, 'warning');").ql();
		pool.tab(2).append("}").ql();
		pool.tab().append("};").ql();
		return pool.toString();
	}
}

class PainelJSService extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelJSService(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.js_service";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		arquivo.add(new Instrucao("Service.$inject = ['Restangular']"));

		Parametros params = new Parametros(new Var("Restangular"));
		FuncaoJS funcao = new FuncaoJS("function Service", params);
		arquivo.add(funcao);

		funcao.add(new Instrucao("var PATH = 'endPointRest'"));
		funcao.ql();
		ReturnJS returnJS = new ReturnJS();
		funcao.add(returnJS);

		returnJS.add(fnPesquisar());
		returnJS.add(fnGerarPDF());

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private Container fnPesquisar() {
		Parametros params = new Parametros(new Var("filtro"));
		FuncaoJS funcao = new FuncaoJS("pesquisar: function", params);
		funcao.add(new Return("", "Restangular.all(PATH).customGET('pesquisar', filtro)"));
		return funcao;
	}

	private Container fnGerarPDF() {
		Parametros params = new Parametros(new Var("filtro"));
		FuncaoJS funcao = new FuncaoJS(",gerarPDF: function", params);
		funcao.add(new Return("",
				"Restangular.all(PATH).withHttpConfig({responseType: \"arraybuffer\"}).customGET('gerarPDF', filtro)"));
		return funcao;
	}
}

class PainelView extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelView(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.view";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();
		if (!atributos.isEmpty()) {
			pool.tab(2).append("<div class='row'>").ql();
			for (Atributo att : atributos) {
				pool.tab(3).append("<div class='col-sm--X'>").ql();
				pool.tab(4).append("{{" + att.getNome() + "}}").ql();
				pool.tab(3).append("</div>").ql();
			}
			pool.tab(2).append("</div>").ql();
		}
		setText(pool.toString());
	}
}

class PainelTest extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelTest(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.test";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.add(new Import("org.junit.Test"));
			arquivo.add(new Import("org.junit.runner.RunWith"));
			arquivo.add(new Import("org.mockito.InjectMocks"));
			arquivo.add(new Import("org.mockito.Mock"));
			arquivo.add(new Import("org.mockito.junit.MockitoJUnitRunner"));
			arquivo.ql();

			Anotacao runWith = new Anotacao("RunWith", "MockitoJUnitRunner.class", true);
			arquivo.add(runWith);
		}

		Classe classe = new Classe("Test");
		arquivo.add(classe);

		Anotacao injectMocks = new Anotacao("InjectMocks", null, true);
		classe.add(injectMocks);
		Campo service = new Campo(AtributoPagina.SERVICE);
		classe.add(service);
		classe.ql();

		Parametros params = new Parametros();
		Funcao funcao = new Funcao(AtributoPagina.PUBLIC, "void", AtributoPagina.PESQUISAR + "Test", params);
		funcao.add(new Comentario("..."));

		classe.add(new Anotacao("Test", null, true));
		classe.add(funcao);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}
}