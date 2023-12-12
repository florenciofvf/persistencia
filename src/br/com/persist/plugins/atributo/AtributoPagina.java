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
import javax.swing.JFileChooser;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.ArquivoUtil;
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
import br.com.persist.plugins.atributo.aux.Container;
import br.com.persist.plugins.atributo.aux.Else;
import br.com.persist.plugins.atributo.aux.Espaco;
import br.com.persist.plugins.atributo.aux.Funcao;
import br.com.persist.plugins.atributo.aux.FuncaoInter;
import br.com.persist.plugins.atributo.aux.FuncaoJS;
import br.com.persist.plugins.atributo.aux.If;
import br.com.persist.plugins.atributo.aux.Import;
import br.com.persist.plugins.atributo.aux.Interface;
import br.com.persist.plugins.atributo.aux.InvocaProm;
import br.com.persist.plugins.atributo.aux.Linha;
import br.com.persist.plugins.atributo.aux.MetodoGet;
import br.com.persist.plugins.atributo.aux.MetodoSet;
import br.com.persist.plugins.atributo.aux.Parametros;
import br.com.persist.plugins.atributo.aux.ReturnJS;
import br.com.persist.plugins.atributo.aux.Tipo;
import br.com.persist.plugins.atributo.aux.Var;
import br.com.persist.plugins.atributo.aux.VarObjJS;

public class AtributoPagina extends Panel {
	public static final String APPLICATION_JSON = "{MediaType.APPLICATION_JSON}";
	public static final Import IMPORT_LIST = new Import("java.util.List");
	public static final String STR_SERVICE = "Service";
	public static final Tipo SERVICE = new Tipo(STR_SERVICE, "service");
	public static final Tipo FILTER = new Tipo("Filter", "filter");
	public static final String PESQUISAR = "pesquisar";
	public static final String LIST_DTO = "List<DTO>";
	public static final String FILTRO = "filtro";
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
			private Action modelIdAcao = acaoMenu("label.ler_id", Icones.FIELDS);
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
				addButton(modelIdAcao);
				modelIdAcao.setActionListener(e -> lerArquivo());
				tabelaAcao.setActionListener(e -> carregar());
			}

			Action acaoMenu(String chave, Icon icon) {
				return Action.acaoMenu(AtributoMensagens.getString(chave), icon);
			}

			Action acaoIcon(String chave, Icon icon) {
				return Action.acaoIcon(AtributoMensagens.getString(chave), icon);
			}

			@Override
			protected void limpar() {
				Atributo att = new Atributo();
				att.setNome("nome");
				att.setRotulo("Rotulo");
				att.setClasse("Classe");
				att.setViewToBack("[nomeFuncaoJS]");
				setText(att);
			}

			private void setText(Atributo... atributos) {
				try {
					StringWriter sw = new StringWriter();
					XMLUtil util = new XMLUtil(sw);
					util.prologo();
					util.abrirTag2("att");
					for (Atributo att : atributos) {
						att.salvar(util);
					}
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

			private void lerArquivo() {
				JFileChooser fileChooser = new JFileChooser(AtributoPreferencia.getDirPadraoSelecaoArquivos());
				int i = fileChooser.showOpenDialog(AtributoPagina.this);
				if (i == JFileChooser.APPROVE_OPTION) {
					File sel = fileChooser.getSelectedFile();
					lerArquivo(sel);
				}
			}

			private void lerArquivo(File file) {
				List<String> lista = ArquivoUtil.lerArquivo(file);
				List<Atributo> atributos = new ArrayList<>();
				for (String string : lista) {
					List<String> ngs = Util.extrairValorNgModel(string);
					for (String ng : ngs) {
						atributos.add(criarAtributo(ng));
					}
				}
				setText(atributos.toArray(new Atributo[0]));
			}

			private Atributo criarAtributo(String string) {
				int pos = string.lastIndexOf(".");
				String nome = pos != -1 ? string.substring(pos + 1) : string;
				Atributo att = new Atributo();
				att.setClasse("String");
				att.setNome(nome);
				return att;
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
	protected final TextField textField2 = new TextField(15);
	protected final TextField textField = new TextField(15);
	protected final JTextPane textArea = new JTextPane();
	private final Toolbar toolbar = new Toolbar();
	private final AtributoPagina pagina;

	AbstratoPanel(AtributoPagina pagina) {
		this.pagina = pagina;
		montarLayout();
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
			add(textField);
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

	protected void addTextField2() {
		toolbar.add(textField2);
	}

	protected String getValorTextField(String padrao) {
		return Util.isEmpty(textField.getText()) ? padrao : textField.getText();
	}

	protected String getValorTextField2(String padrao) {
		return Util.isEmpty(textField2.getText()) ? padrao : textField2.getText();
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
		Classe classe = new Classe(getValorTextField("DTO"));

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
			arquivo.addImport("javax.ws.rs.QueryParam").ql();
		}

		Classe classe = new Classe(getValorTextField("Filter"));
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
			arquivo.add(AtributoPagina.IMPORT_LIST).ql();
			arquivo.addImport("javax.inject.Inject").ql();
			arquivo.addImport("javax.ws.rs.Consumes");
			arquivo.addImport("javax.ws.rs.Produces").ql();
			arquivo.addImport("javax.ws.rs.core.MediaType").ql();
			arquivo.addImport("javax.ws.rs.BeanParam");
			arquivo.addImport("javax.ws.rs.GET");
			arquivo.addImport("javax.ws.rs.Path");
			arquivo.addImport("javax.ws.rs.Produces");
			arquivo.addImport("javax.ws.rs.QueryParam");
			arquivo.addImport("javax.ws.rs.core.MediaType").ql();
			arquivo.addComentario("br.gov.dpf.framework.seguranca.RestSeguranca;").ql();

			Anotacao path = new Anotacao("Path", Util.citar2("endPointRest"), true);
			arquivo.add(path);
		}

		Classe classe = new Classe(getValorTextField("Rest") + " extends ApplicationRest");
		arquivo.add(classe);

		injetar(classe, AtributoPagina.SERVICE).ql();
		injetar(classe, new Tipo("ServicePDF", "servicePDF")).ql();
		criarGetListaDTO(classe).ql();
		criarGetGerarPDF(classe);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private Classe injetar(Classe classe, Tipo tipo) {
		Anotacao inject = new Anotacao("Inject", null, true);
		classe.add(inject);
		Campo service = new Campo(tipo);
		classe.add(service);
		return classe;
	}

	private Classe criarGetListaDTO(Classe classe) {
		classe.add(new Anotacao("GET", null, true));
		classe.add(new Anotacao("Path", Util.citar2("endPointMetodo"), true));
		classe.add(new Anotacao("Consumes", AtributoPagina.APPLICATION_JSON, true));
		classe.add(new Anotacao("Produces", AtributoPagina.APPLICATION_JSON, true));

		Funcao funcao = new Funcao(AtributoPagina.PUBLIC, AtributoPagina.LIST_DTO, AtributoPagina.PESQUISAR,
				beanParam());
		funcao.addReturn("service.pesquisar(filter)");
		classe.add(funcao);
		return classe;
	}

	private Classe criarGetGerarPDF(Classe classe) {
		classe.add(new Anotacao("GET", null, true));
		classe.add(new Anotacao("Path", Util.citar2("endPointMetodo"), true));
		classe.add(new Anotacao("Consumes", AtributoPagina.APPLICATION_JSON, true));
		classe.add(new Anotacao("Produces", "{MediaType.APPLICATION_OCTET_STREAM}", true));

		Funcao funcao = new Funcao(AtributoPagina.PUBLIC, "Response", "gerarPDF", beanParam());
		funcao.addInstrucao("DadosDTO dto = service.recuperarDTO(filter)");
		funcao.addInstrucao("byte[] bytes = servicePDF.gerarPDF(dto)").ql();
		funcao.addInstrucao("ResponseBuilder response = Response.ok(bytes)");
		funcao.addInstrucao("response.header(\"Content-Disposition\", \"attachment;filename=arquivo.dpf\")");
		funcao.addInstrucao("response.header(\"Content-type\", MediaType.APPLICATION_OCTET_STREAM)");
		funcao.addReturn("response.build()");
		classe.add(funcao);
		return classe;
	}

	private Parametros beanParam() {
		Parametros params = new Parametros(new Anotacao("BeanParam", null));
		params.add(new Espaco());
		params.add(AtributoPagina.FILTER);
		return params;
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
			arquivo.add(AtributoPagina.IMPORT_LIST).ql();
			arquivo.addImport("javax.ejb.Local").ql();

			Anotacao local = new Anotacao("Local", null, true);
			arquivo.add(local);
		}

		Interface interfac = new Interface(getValorTextField(AtributoPagina.STR_SERVICE));
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
			arquivo.add(AtributoPagina.IMPORT_LIST).ql();
			arquivo.addImport("javax.ejb.LocalBean");
			arquivo.addImport("javax.ejb.Stateless");
			arquivo.addImport("javax.ejb.TransactionManagement");
			arquivo.addImport("javax.ejb.TransactionManagementType").ql();

			Anotacao stateless = new Anotacao("Stateless", null, true);
			arquivo.add(stateless);
			Anotacao localBean = new Anotacao("LocalBean", null, true);
			arquivo.add(localBean);
			Anotacao transaction = new Anotacao("TransactionManagement", "TransactionManagementType.CONTAINER", true);
			arquivo.add(transaction);
		}

		Classe classe = new Classe(getValorTextField("Bean") + " implements Service");
		arquivo.add(classe);

		Anotacao inject = new Anotacao("Inject", null, true);
		classe.add(inject);
		Campo service = new Campo(new Tipo("DAO", "dao"));
		classe.add(service).ql();

		Parametros params = new Parametros(AtributoPagina.FILTER);
		Funcao funcao = new Funcao(AtributoPagina.PUBLIC, AtributoPagina.LIST_DTO, AtributoPagina.PESQUISAR, params);
		funcao.addReturn("dao.pesquisar(filter)");
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
			arquivo.add(AtributoPagina.IMPORT_LIST).ql();
		}

		Interface interfac = new Interface(getValorTextField("DAO"));
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
			arquivo.addImport("java.util.ArrayList");
			arquivo.add(AtributoPagina.IMPORT_LIST).ql();
			arquivo.addImport("javax.persistence.EntityManager");
			arquivo.addImport("javax.persistence.PersistenceContext").ql();
		}

		Classe classe = new Classe(getValorTextField("DAOImpl") + " implements DAO");
		arquivo.add(classe);

		Anotacao context = new Anotacao("PersistenceContext", "unitName = " + Util.citar2("nomeUnit"), true);
		classe.add(context);
		Campo entityManager = new Campo(new Tipo("EntityManager", "entityManager"));
		classe.add(entityManager).ql();

		Parametros params = new Parametros(AtributoPagina.FILTER);
		Funcao funcao = new Funcao(AtributoPagina.PUBLIC, AtributoPagina.LIST_DTO, AtributoPagina.PESQUISAR, params);
		funcao.addInstrucao("List<DTO> resp = new ArrayList<>()");
		funcao.addComentario("entityManager.find...").ql();
		funcao.addReturn("resp");
		classe.add(funcao);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}
}

class PainelJSController extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelJSController(AtributoPagina pagina) {
		super(pagina);
		addTextField2();
	}

	@Override
	String getChaveTitulo() {
		return "label.js_controller";
	}

	@Override
	void gerar(List<Atributo> atributos) {
		StringPool pool = new StringPool();

		String filtro = getValorTextField2(AtributoPagina.FILTRO);
		String nome = getValorTextField("Controller");

		Arquivo arquivo = new Arquivo();
		arquivo.addInstrucao(nome + ".$inject = ['$scope', '$state', 'NgTableParams', 'Service']");

		String string = ", ";
		Parametros params = new Parametros();
		params.addVar("$scope").append(string);
		params.addVar("$state").append(string);
		params.addVar("NgTableParams").append(string);
		params.addVar(AtributoPagina.STR_SERVICE);
		FuncaoJS funcao = new FuncaoJS("function " + nome, params);
		arquivo.add(funcao);

		funcao.addInstrucao("var vm = this").ql();
		funcao.addInstrucao("vm.pesquisados = new NgTableParams()");
		funcao.addInstrucao("vm." + filtro + " = {}").ql();

		funcao.add(fnLimparFiltro(filtro)).ql();
		funcao.add(fnGetTime()).ql();
		funcao.add(fnParam(filtro, atributos)).ql();
		funcao.add(fnValidar(filtro, atributos)).ql();
		funcao.add(fnPesquisa(filtro)).ql();
		funcao.add(fnPDF(filtro));

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private Container fnLimparFiltro(String filtro) {
		FuncaoJS funcao = new FuncaoJS("vm.limpar" + Util.capitalize(filtro) + " = function", new Parametros());
		funcao.addInstrucao("vm." + filtro + " = {}").ql();
		return funcao;
	}

	private Container fnGetTime() {
		FuncaoJS funcao = new FuncaoJS("function getTime", new Parametros(new Var("obj")));
		If iff = new If("obj instanceof Date", null);
		iff.addReturn("obj.getTime()");
		funcao.add(iff);
		funcao.addReturn("null");
		return funcao;
	}

	private Container fnParam(String filtro, List<Atributo> atributos) {
		FuncaoJS funcao = new FuncaoJS("function criarParam" + Util.capitalize(filtro), new Parametros());
		funcao.add(objParam(filtro, atributos)).ql();
		funcao.addReturn("param");
		return funcao;
	}

	private Container objParam(String filtro, List<Atributo> atributos) {
		VarObjJS obj = new VarObjJS("param");
		for (int i = 0; i < atributos.size(); i++) {
			Atributo att = atributos.get(i);
			obj.addFragmento(att.getNome() + ": " + att.gerarViewToBack(filtro));
			if (i + 1 < atributos.size()) {
				obj.append(",");
			}
			obj.ql();
		}
		return obj;
	}

	private Container fnValidar(String filtro, List<Atributo> atributos) {
		FuncaoJS funcao = new FuncaoJS("function validar" + Util.capitalize(filtro), new Parametros());
		if (atributos.size() > 1) {
			funcao.add(ifVazios(filtro, atributos)).ql();
		}
		for (int i = 0; i < atributos.size(); i++) {
			Atributo att = atributos.get(i);
			funcao.add(ifObrigatorio(filtro, att));
			if (i + 1 < atributos.size()) {
				funcao.ql();
			}
		}
		if (!atributos.isEmpty()) {
			funcao.ql();
		}
		funcao.addReturn("null");
		return funcao;
	}

	private Container ifVazios(String filtro, List<Atributo> atributos) {
		If iff = new If(vazios(filtro, atributos), null);
		iff.addReturn("'Favor preencher pelo ao menos um campo de pesquisa'");
		return iff;
	}

	private static String vazios(String filtro, List<Atributo> atributos) {
		StringBuilder sb = new StringBuilder();
		for (Atributo att : atributos) {
			if (sb.length() > 0) {
				sb.append(" && ");
			}
			sb.append(att.gerarIsVazioJS(filtro));
		}
		return sb.toString();
	}

	private Container ifObrigatorio(String filtro, Atributo att) {
		If iff = new If(att.gerarIsVazioJS(filtro), null);
		String campo = Util.isEmpty(att.getRotulo()) ? att.getNome() : att.getRotulo();
		iff.addReturn("'Campo " + campo + " Obrigat\u00F3rio.'");
		return iff;
	}

	private Container fnPesquisa(String filtro) {
		FuncaoJS funcao = new FuncaoJS("vm.pesquisar = function", new Parametros());
		funcao.addInstrucao("var msg = validar" + Util.capitalize(filtro) + "()");

		Else elsee = new Else();
		elsee.addComentario("Msg.error(msg);");
		elsee.addComentario("$scope.$emit('msg', msg, null, 'warning');");

		If iff = new If("isVazio(msg)", elsee);
		funcao.add(iff);

		InvocaProm invocaProm = new InvocaProm(
				"Service.pesquisar(criarParam" + Util.capitalize(filtro) + "()).then(function(result) {");
		iff.add(invocaProm);

		invocaProm.addInstrucao("var lista = result.data");
		invocaProm.addInstrucao("vm.pesquisados.settings().dataset = lista");
		invocaProm.addInstrucao("vm.pesquisados.reload()");

		If ifLength = new If("lista.length === 0", null);
		invocaProm.add(ifLength);
		ifLength.addComentario("Msg.info('Nenhum registro encontrado');");
		ifLength.addComentario("$scope.$emit('msg', 'Nenhum registro encontrado', null, 'warning');");

		return funcao;
	}

	private Container fnPDF(String filtro) {
		FuncaoJS funcao = new FuncaoJS("vm.gerarPDF = function", new Parametros());
		funcao.addInstrucao("var msg = validar" + Util.capitalize(filtro) + "()");

		Else elsee = new Else();
		elsee.addComentario("Msg.error(msg);");
		elsee.addComentario("$scope.$emit('msg', msg, null, 'warning');");

		If iff = new If("isVazio(msg)", elsee);
		funcao.add(iff);

		InvocaProm invocaProm = new InvocaProm(
				"Service.gerarPDF(criarParam" + Util.capitalize(filtro) + "()).then(function(result) {");
		iff.add(invocaProm);

		invocaProm.addInstrucao("var file = new Blob([result.data], {type: 'application/pdf'})");
		invocaProm.addInstrucao("var downloadLink = angular.element('<a></a>')");
		invocaProm.addInstrucao("downloadLink.attr('href', window.URL.createObjectURL(file))");
		invocaProm.addInstrucao("downloadLink.attr('download', \"arquivo.pdf\")");
		invocaProm.addInstrucao("var link = downloadLink[0]");
		invocaProm.addInstrucao("document.body.appendChild(link)");
		invocaProm.addInstrucao("link.click()");
		invocaProm.addInstrucao("document.body.removeChild(link)");

		return funcao;
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

		String nome = getValorTextField(AtributoPagina.STR_SERVICE);

		Arquivo arquivo = new Arquivo();
		arquivo.addInstrucao(nome + ".$inject = ['Restangular']");

		Parametros params = new Parametros(new Var("Restangular"));
		FuncaoJS funcao = new FuncaoJS("function " + nome, params);
		arquivo.add(funcao);

		funcao.addInstrucao("var PATH = 'endPointRest'").ql();
		ReturnJS returnJS = new ReturnJS();
		funcao.add(returnJS);

		returnJS.add(fnPesquisar());
		returnJS.add(fnGerarPDF());

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private Container fnPesquisar() {
		Parametros params = new Parametros(new Var(AtributoPagina.FILTRO));
		FuncaoJS funcao = new FuncaoJS("pesquisar: function", params);
		funcao.addReturn("Restangular.all(PATH).customGET('pesquisar', filtro)");
		return funcao;
	}

	private Container fnGerarPDF() {
		Parametros params = new Parametros(new Var(AtributoPagina.FILTRO));
		FuncaoJS funcao = new FuncaoJS(",gerarPDF: function", params);
		funcao.addReturn(
				"Restangular.all(PATH).withHttpConfig({responseType: \"arraybuffer\"}).customGET('gerarPDF', filtro)");
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

		String filtro = getValorTextField2(AtributoPagina.FILTRO);
		String funcao = getValorTextField(AtributoPagina.PESQUISAR);

		if (!atributos.isEmpty()) {
			pool.tab(2).append("<div class='row'>").ql();
			for (Atributo att : atributos) {
				pool.tab(3).append("<div class='col-sm--X'>").ql();
				pool.tab(4).append("{{" + att.getNome() + "}}").ql();
				pool.tab(3).append("</div>").ql();
			}
			pool.tab(2).append("</div>").ql();
		}

		pool.ql();
		pool.append("<button id=\"pesquisar\" ng-click=\"vm." + funcao
				+ "()\" class=\"btn btn--primary btn--sm m-l-0-5\"><i class=\"i i-file-pdf-o\"></i>Pesquisar</button>");
		pool.append("<button id=\"limpar\" ng-click=\"vm.limpar" + filtro
				+ "()\" class=\"btn btn--default btn--sm m-l-0-5\">Limpar</button>");

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
			arquivo.addImport("org.junit.Test");
			arquivo.addImport("org.junit.runner.RunWith");
			arquivo.addImport("org.mockito.InjectMocks");
			arquivo.addImport("org.mockito.Mock");
			arquivo.addImport("org.mockito.junit.MockitoJUnitRunner").ql();

			Anotacao runWith = new Anotacao("RunWith", "MockitoJUnitRunner.class", true);
			arquivo.add(runWith);
		}

		Classe classe = new Classe(getValorTextField("Test"));
		arquivo.add(classe);

		Anotacao injectMocks = new Anotacao("InjectMocks", null, true);
		classe.add(injectMocks);
		Campo service = new Campo(AtributoPagina.SERVICE);
		classe.add(service).ql();

		Parametros params = new Parametros();
		Funcao funcao = new Funcao(AtributoPagina.PUBLIC, "void", AtributoPagina.PESQUISAR + "Test", params);
		funcao.addComentario("...");

		classe.add(new Anotacao("Test", null, true));
		classe.add(funcao);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}
}