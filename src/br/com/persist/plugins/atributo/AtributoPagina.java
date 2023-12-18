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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import br.com.persist.plugins.atributo.aux.Interface;
import br.com.persist.plugins.atributo.aux.InvocaProm;
import br.com.persist.plugins.atributo.aux.MetodoGet;
import br.com.persist.plugins.atributo.aux.MetodoSet;
import br.com.persist.plugins.atributo.aux.Parametros;
import br.com.persist.plugins.atributo.aux.ReturnJS;
import br.com.persist.plugins.atributo.aux.Tipo;
import br.com.persist.plugins.atributo.aux.Var;
import br.com.persist.plugins.atributo.aux.VarObjJS;

public class AtributoPagina extends Panel {
	private static final long serialVersionUID = 1L;
	private final PainelAtributo painelAtributo;
	private final PainelFichario painelFichario;
	private transient Mapa mapa;

	public AtributoPagina(File file) {
		painelAtributo = new PainelAtributo(file);
		painelFichario = new PainelFichario(this);
		montarLayout();
		abrir();
	}

	public Mapa getMapa() {
		if (mapa == null) {
			mapa = new Mapa();
		}
		return mapa;
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
				String s = Constantes.SEP + "valor" + Constantes.SEP;
				Atributo att = new Atributo();
				att.setNome("nome");
				att.setRotulo("Rotulo");
				att.setClasse("Classe");
				att.setViewToBack("[funcaoJS(" + s + ")]");
				criarNovoArquivo(att);
			}

			private void criarNovoArquivo(Atributo... atributos) {
				try {
					Mapa mapaHierarquia = criarMapaHierarquia(atributos);
					textArea.setText(mapaHierarquia.toString());
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
					if (!Util.isEmpty(textArea.getText())) {
						AtributoHandlerImpl handler = new AtributoHandlerImpl();
						AtributoProcessador processador = new AtributoProcessador(handler, textArea.getText());
						processador.processar();
						mapa = handler.getRaiz();
						if (mapa != null && mapa.get(AtributoConstantes.ATRIBUTOS) != null) {
							Mapa mapAtributos = (Mapa) mapa.get(AtributoConstantes.ATRIBUTOS);
							List<Atributo> atributos = new ArrayList<>();
							for (Object valor : mapAtributos.getValores()) {
								Atributo att = new Atributo();
								att.aplicar((Mapa) valor);
								atributos.add(att);
							}
							tabela.setModel(new AtributoModelo(atributos));
							Util.ajustar(tabela, getGraphics());
						}
					}
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
				criarNovoArquivo(atributos.toArray(new Atributo[0]));
			}

			private Atributo criarAtributo(String string) {
				int pos = string.lastIndexOf(".");
				String nome = pos != -1 ? string.substring(pos + 1) : string;
				Atributo att = new Atributo();
				att.setClasse("String");
				att.setNome(nome);
				return att;
			}

			private Mapa criarMapaHierarquia(Atributo... atributos) {
				Mapa resp = new Mapa();
				resp.put(AtributoConstantes.ATRIBUTOS, criarMapaAtributos(atributos));
				resp.put(AtributoConstantes.FILTRO, AtributoConstantes.FILTRO);
				resp.put(AtributoConstantes.CONTROLLER_JS, criarMapa(AtributoConstantes.CONTROLLER_JS,
						new ChaveValor(AtributoConstantes.LIMPAR_FILTRO, AtributoConstantes.LIMPAR_FILTRO)));
				resp.put(AtributoConstantes.SERVICE_JS, criarMapa(AtributoConstantes.SERVICE_JS));
				resp.put(AtributoConstantes.DTO, AtributoConstantes.DTO);
				resp.put(AtributoConstantes.FILTER, AtributoConstantes.FILTER);
				resp.put(AtributoConstantes.REST, criarMapa(AtributoConstantes.REST,
						new ChaveValor(AtributoConstantes.END_POINT, AtributoConstantes.END_POINT)));
				resp.put(AtributoConstantes.SERVICE, criarMapa(AtributoConstantes.SERVICE));
				resp.put(AtributoConstantes.BEAN, AtributoConstantes.BEAN);
				resp.put(AtributoConstantes.DAO, criarMapa(AtributoConstantes.DAO));
				resp.put(AtributoConstantes.DAO_IMPL, AtributoConstantes.DAO_IMPL);
				resp.put(AtributoConstantes.TEST, criarMapa(AtributoConstantes.TEST));
				return resp;
			}

			private Mapa criarMapaAtributos(Atributo... atributos) {
				Mapa resp = new Mapa();
				for (Atributo att : atributos) {
					resp.put(att.getNome(), att.criarMapa());
				}
				return resp;
			}

			private Mapa criarMapa(String arquivo, ChaveValor... cvs) {
				Mapa resp = new Mapa();
				resp.put(AtributoConstantes.COMPONENTE, Util.capitalize(arquivo));
				resp.put(AtributoConstantes.PESQUISAR, AtributoConstantes.PESQUISAR);
				resp.put(AtributoConstantes.EXPORTAR, AtributoConstantes.EXPORTAR);
				for (ChaveValor cv : cvs) {
					resp.put(cv.getChave(), cv.getValor());
				}
				return resp;
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
		addAba(new PainelView(pagina));
		addAba(new PainelControllerJS(pagina));
		addAba(new PainelParamJS(pagina));
		addAba(new PainelValidarJS(pagina));
		addAba(new PainelServiceJS(pagina));
		addAba(new PainelDTO(pagina));
		addAba(new PainelFilter(pagina));
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
		this.pagina = pagina;
		montarLayout();
	}

	public AtributoPagina getPagina() {
		return pagina;
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
			gerar(pagina.getMapa(), lista);
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

	abstract void gerar(Mapa raiz, List<Atributo> atributos);

	abstract String getChaveTitulo();
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
	void gerar(Mapa raiz, List<Atributo> atributos) {
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

		Mapa mapaControllerJS = AtributoUtil.getMapaControllerJS(raiz);

		if (mapaControllerJS == null) {
			return;
		}

		pool.ql();
		pool.tab().append("<button id=\"pesquisar\" ng-click=\"vm." + AtributoUtil.getPesquisar(mapaControllerJS)
				+ "()\" class=\"btn btn--primary btn--sm m-l-0-5\"><i class=\"i i-search\"></i>Pesquisar</button>")
				.ql();
		pool.tab().append("<button id=\"exportar\" ng-click=\"vm." + AtributoUtil.getExportar(mapaControllerJS)
				+ "()\" class=\"btn btn--primary btn--sm m-l-0-5\"><i class=\"i i-file-pdf-o\"></i>Exportar PDF</button>")
				.ql();
		pool.tab()
				.append("<button id=\"limpar\" ng-click=\"vm."
						+ mapaControllerJS.getString(AtributoConstantes.LIMPAR_FILTRO)
						+ "()\" class=\"btn btn--default btn--sm m-l-0-5\">Limpar</button>");

		setText(pool.toString());
	}
}

class PainelControllerJS extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelControllerJS(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.controller_js";
	}

	@Override
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		String filtro = AtributoUtil.getFiltro(raiz);

		Mapa mapaControllerJS = AtributoUtil.getMapaControllerJS(raiz);
		Mapa mapaServiceJS = AtributoUtil.getMapaServiceJS(raiz);

		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		Arquivo arquivo = criarArquivo(mapaControllerJS, mapaServiceJS);
		FuncaoJS funcao = (FuncaoJS) arquivo.get(1);
		funcao.addInstrucao("var vm = this").ql();
		funcao.addInstrucao("vm.pesquisados = new NgTableParams()");
		funcao.addInstrucao("vm." + filtro + " = {}").ql();

		funcao.add(fnLimparFiltro(mapaControllerJS, filtro)).ql();
		funcao.add(fnPesquisa(mapaControllerJS, mapaServiceJS, filtro)).ql();
		funcao.add(fnPDF(mapaControllerJS, mapaServiceJS, filtro)).ql();
		funcao.add(fnProcessarFile());

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	protected Arquivo criarArquivo(Mapa mapaControllerJS, Mapa mapaServiceJS) {
		Arquivo arquivo = new Arquivo();
		arquivo.addInstrucao(
				AtributoUtil.getComponente(mapaControllerJS) + ".$inject = ['$scope', '$state', 'NgTableParams', '"
						+ AtributoUtil.getComponente(mapaServiceJS) + "']");

		String string = ", ";
		Parametros params = new Parametros();
		params.addVar("$scope").append(string);
		params.addVar("$state").append(string);
		params.addVar("NgTableParams").append(string);
		params.addVar(AtributoUtil.getComponente(mapaServiceJS));
		FuncaoJS funcao = new FuncaoJS(AtributoConstantes.FUNCTION + AtributoUtil.getComponente(mapaControllerJS),
				params);
		arquivo.add(funcao);

		return arquivo;
	}

	private Container fnLimparFiltro(Mapa mapaControllerJS, String filtro) {
		FuncaoJS funcao = new FuncaoJS(
				"vm." + mapaControllerJS.getString(AtributoConstantes.LIMPAR_FILTRO) + AtributoConstantes.FUNCTION2,
				new Parametros());
		funcao.addInstrucao("vm." + filtro + " = {}");
		return funcao;
	}

	private Container fnPesquisa(Mapa mapaControllerJS, Mapa mapaServiceJS, String filtro) {
		FuncaoJS funcao = new FuncaoJS(
				"vm." + AtributoUtil.getPesquisar(mapaControllerJS) + AtributoConstantes.FUNCTION2, new Parametros());
		funcao.addInstrucao("var msg = validar" + Util.capitalize(filtro) + "()");

		Else elsee = new Else();
		elsee.addComentario("Msg.error(msg);");
		elsee.addComentario("$scope.$emit('msg', msg, null, 'warning');");

		If iff = new If("isVazio(msg)", elsee);
		funcao.add(iff);

		InvocaProm invocaProm = new InvocaProm(
				AtributoUtil.getComponente(mapaServiceJS) + "." + AtributoUtil.getPesquisar(mapaServiceJS)
						+ "(criarParam" + Util.capitalize(filtro) + "()).then(function(result) {");
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

	private Container fnPDF(Mapa mapaControllerJS, Mapa mapaServiceJS, String filtro) {
		FuncaoJS funcao = new FuncaoJS(
				"vm." + AtributoUtil.getExportar(mapaControllerJS) + AtributoConstantes.FUNCTION2, new Parametros());
		funcao.addInstrucao("var msg = validar" + Util.capitalize(filtro) + "()");

		Else elsee = new Else();
		elsee.addComentario("Msg.error(msg);");
		elsee.addComentario("$scope.$emit('msg', msg, null, 'warning');");

		If iff = new If("isVazio(msg)", elsee);
		funcao.add(iff);

		InvocaProm invocaProm = new InvocaProm(
				AtributoUtil.getComponente(mapaServiceJS) + "." + AtributoUtil.getExportar(mapaServiceJS)
						+ "(criarParam" + Util.capitalize(filtro) + "()).then(function(result) {");
		iff.add(invocaProm);

		invocaProm.addComentario("var file = new Blob([result.data], {type: 'application/octet-stream'});");
		invocaProm.addComentario("processarFile(file, \"arquivo.xls\");");

		invocaProm.addInstrucao("var file = new Blob([result.data], {type: 'application/pdf'})");
		invocaProm.addInstrucao("processarFile(file, \"arquivo.pdf\")");

		return funcao;
	}

	private Container fnProcessarFile() {
		Parametros params = new Parametros();
		params.addVar("file").append(", ").addVar("arquivo");
		FuncaoJS funcao = new FuncaoJS("function processarFile", params);
		funcao.addInstrucao("var downloadLink = angular.element('<a></a>')");
		funcao.addInstrucao("downloadLink.attr('href', window.URL.createObjectURL(file))");
		funcao.addInstrucao("downloadLink.attr('download', arquivo)");
		funcao.addInstrucao("var link = downloadLink[0]");
		funcao.addInstrucao("document.body.appendChild(link)");
		funcao.addInstrucao("link.click()");
		funcao.addInstrucao("document.body.removeChild(link)");
		return funcao;
	}
}

class PainelParamJS extends PainelControllerJS {
	private static final long serialVersionUID = 1L;

	PainelParamJS(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.param_js";
	}

	@Override
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		String filtro = AtributoUtil.getFiltro(raiz);

		Mapa mapaControllerJS = AtributoUtil.getMapaControllerJS(raiz);
		Mapa mapaServiceJS = AtributoUtil.getMapaServiceJS(raiz);

		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		Arquivo arquivo = criarArquivo(mapaControllerJS, mapaServiceJS);
		FuncaoJS funcao = (FuncaoJS) arquivo.get(1);
		funcao.add(fnParam(filtro, atributos));

		arquivo.gerar(0, pool);
		setText(pool.toString());
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
}

class PainelValidarJS extends PainelControllerJS {
	private static final long serialVersionUID = 1L;

	PainelValidarJS(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.validar_js";
	}

	@Override
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		String filtro = AtributoUtil.getFiltro(raiz);

		Mapa mapaControllerJS = AtributoUtil.getMapaControllerJS(raiz);
		Mapa mapaServiceJS = AtributoUtil.getMapaServiceJS(raiz);

		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		Arquivo arquivo = criarArquivo(mapaControllerJS, mapaServiceJS);
		FuncaoJS funcao = (FuncaoJS) arquivo.get(1);

		funcao.add(fnGetTime()).ql();
		funcao.add(fnValidar(filtro, atributos));

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private Container fnGetTime() {
		FuncaoJS funcao = new FuncaoJS("function getTime", new Parametros(new Var("obj")));
		If iff = new If("obj instanceof Date", null);
		iff.addReturn("obj.getTime()");
		funcao.add(iff);
		funcao.addReturn("null");
		return funcao;
	}

	private Container fnValidar(String filtro, List<Atributo> atributos) {
		FuncaoJS funcao = new FuncaoJS("function validar" + Util.capitalize(filtro), new Parametros());
		funcao.addComentario("$scope.$emit('msgClear');");
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
}

class PainelServiceJS extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelServiceJS(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.service_js";
	}

	@Override
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Mapa mapaServiceJS = AtributoUtil.getMapaServiceJS(raiz);
		Mapa mapaRest = AtributoUtil.getMapaRest(raiz);

		if (mapaServiceJS == null || mapaRest == null) {
			return;
		}

		Arquivo arquivo = new Arquivo();
		arquivo.addInstrucao(AtributoUtil.getComponente(mapaServiceJS) + ".$inject = ['Restangular']");

		Parametros params = new Parametros(new Var("Restangular"));
		FuncaoJS funcao = new FuncaoJS(AtributoConstantes.FUNCTION + AtributoUtil.getComponente(mapaServiceJS), params);
		arquivo.add(funcao);

		funcao.addInstrucao("var PATH = '" + mapaRest.getString(AtributoConstantes.END_POINT) + "'").ql();
		ReturnJS returnJS = new ReturnJS();
		funcao.add(returnJS);

		returnJS.add(fnPesquisar(mapaServiceJS, mapaRest));
		returnJS.add(fnGerarPDF(mapaServiceJS, mapaRest));

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private Container fnPesquisar(Mapa mapaServiceJS, Mapa mapaRest) {
		Parametros params = new Parametros(new Var(AtributoConstantes.FILTRO));
		FuncaoJS funcao = new FuncaoJS(AtributoUtil.getPesquisar(mapaServiceJS) + ": function", params);
		funcao.addReturn("Restangular.all(PATH).customGET('" + AtributoUtil.getPesquisar(mapaRest) + "', filtro)");
		return funcao;
	}

	private Container fnGerarPDF(Mapa mapaServiceJS, Mapa mapaRest) {
		Parametros params = new Parametros(new Var(AtributoConstantes.FILTRO));
		FuncaoJS funcao = new FuncaoJS("," + AtributoUtil.getExportar(mapaServiceJS) + ": function", params);
		funcao.addReturn("Restangular.all(PATH).withHttpConfig({responseType: \"arraybuffer\"}).customGET('"
				+ AtributoUtil.getExportar(mapaRest) + "', filtro)");
		return funcao;
	}
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
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();
		Classe classe = new Classe(AtributoUtil.getDTO(raiz));

		for (Atributo att : atributos) {
			Campo campo = new Campo(att.criarTipo());
			classe.add(campo);
		}

		for (Atributo att : atributos) {
			Tipo tipo = att.criarTipo();
			MetodoGet get = new MetodoGet(tipo);
			MetodoSet set = new MetodoSet(tipo);
			classe.ql().add(get);
			classe.ql().add(set);
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
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.addImport("javax.ws.rs.QueryParam").ql();
		}

		Classe classe = new Classe(AtributoUtil.getFilter(raiz));
		arquivo.add(classe);

		int i = 0;
		for (Atributo att : atributos) {
			if (i++ > 0) {
				classe.ql();
			}
			classe.add(new Anotacao("QueryParam", Util.citar2(att.getNome()), true));
			Campo campo = new Campo(att.criarTipo());
			classe.add(campo);
		}

		for (Atributo att : atributos) {
			Tipo tipo = att.criarTipo();
			MetodoGet get = new MetodoGet(tipo);
			MetodoSet set = new MetodoSet(tipo);
			classe.ql().add(get);
			classe.ql().add(set);
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
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Mapa mapaService = AtributoUtil.getMapaService(raiz);
		Mapa mapaRest = AtributoUtil.getMapaRest(raiz);

		if (mapaService == null || mapaRest == null) {
			return;
		}

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.add(AtributoConstantes.IMPORT_LIST).ql();
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

			arquivo.add(new Anotacao("Path", Util.citar2(mapaRest.getString(AtributoConstantes.END_POINT)), true));
		}

		Classe classe = new Classe(AtributoUtil.getComponente(mapaRest) + " extends ApplicationRest");
		arquivo.add(classe);

		injetar(classe, new Tipo(AtributoUtil.getComponente(mapaService), "service")).ql();
		injetar(classe, new Tipo(AtributoUtil.getComponente(mapaService) + "PDF", "servicePDF")).ql();
		criarGetListaDTO(raiz, mapaRest, mapaService, classe).ql();
		criarGetGerarPDF(raiz, mapaRest, mapaService, classe);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private Classe injetar(Classe classe, Tipo tipo) {
		classe.add(new Anotacao("Inject", null, true));
		Campo service = new Campo(tipo);
		classe.add(service);
		return classe;
	}

	private Classe criarGetListaDTO(Mapa raiz, Mapa mapaRest, Mapa mapaService, Classe classe) {
		classe.add(new Anotacao("GET", null, true));
		classe.add(new Anotacao("Path", Util.citar2(AtributoUtil.getPesquisar(mapaRest)), true));
		classe.add(new Anotacao("Consumes", AtributoConstantes.APPLICATION_JSON, true));
		classe.add(new Anotacao("Produces", AtributoConstantes.APPLICATION_JSON, true));

		Funcao funcao = new Funcao(AtributoConstantes.PUBLIC, AtributoUtil.getListDTO(raiz),
				AtributoUtil.getPesquisar(mapaRest), beanParam(raiz));
		funcao.addReturn("service." + AtributoUtil.getPesquisarFilter(mapaService));
		classe.add(funcao);
		return classe;
	}

	private Classe criarGetGerarPDF(Mapa raiz, Mapa mapaRest, Mapa mapaService, Classe classe) {
		classe.add(new Anotacao("GET", null, true));
		classe.add(new Anotacao("Path", Util.citar2(AtributoUtil.getExportar(mapaRest)), true));
		classe.add(new Anotacao("Consumes", AtributoConstantes.APPLICATION_JSON, true));
		classe.add(new Anotacao("Produces", "{MediaType.APPLICATION_OCTET_STREAM}", true));

		Funcao funcao = new Funcao(AtributoConstantes.PUBLIC, "Response", AtributoUtil.getExportar(mapaRest),
				beanParam(raiz));
		funcao.addInstrucao(
				AtributoUtil.getListDTO(raiz) + " dtos = service." + AtributoUtil.getPesquisarFilter(mapaService));
		funcao.addInstrucao("byte[] bytes = servicePDF." + AtributoUtil.getExportar(mapaService) + "(dtos)").ql();
		funcao.addInstrucao("ResponseBuilder response = Response.ok(bytes)");
		funcao.addInstrucao("response.header(\"Content-Disposition\", \"attachment;filename=arquivo.pdf\")");
		funcao.addInstrucao("response.header(\"Content-type\", MediaType.APPLICATION_OCTET_STREAM)");
		funcao.addReturn("response.build()");
		classe.add(funcao);
		return classe;
	}

	private Parametros beanParam(Mapa raiz) {
		Parametros params = new Parametros(new Anotacao("BeanParam", null));
		params.add(new Espaco());
		params.add(AtributoUtil.getTipoFilter(raiz));
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
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.add(AtributoConstantes.IMPORT_LIST).ql();
			arquivo.addImport("javax.ejb.Local").ql();

			arquivo.add(new Anotacao("Local", null, true));
		}

		Mapa mapaService = AtributoUtil.getMapaService(raiz);

		if (mapaService == null) {
			return;
		}

		Interface interfac = new Interface(AtributoUtil.getComponente(mapaService));
		arquivo.add(interfac);

		Parametros params = new Parametros(AtributoUtil.getTipoFilter(raiz));
		FuncaoInter funcao = new FuncaoInter(AtributoUtil.getListDTO(raiz), AtributoUtil.getPesquisar(mapaService),
				params);
		interfac.add(funcao).ql();

		params = new Parametros(new Tipo("List<" + AtributoUtil.getDTO(raiz) + ">", "dtos"));
		funcao = new FuncaoInter("byte[]", AtributoUtil.getExportar(mapaService), params);
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
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.add(AtributoConstantes.IMPORT_LIST).ql();
			arquivo.addImport("javax.ejb.LocalBean");
			arquivo.addImport("javax.ejb.Stateless");
			arquivo.addImport("javax.ejb.TransactionManagement");
			arquivo.addImport("javax.ejb.TransactionManagementType").ql();

			arquivo.add(new Anotacao("Stateless", null, true));
			arquivo.add(new Anotacao("LocalBean", null, true));
			arquivo.add(new Anotacao("TransactionManagement", "TransactionManagementType.CONTAINER", true));
		}

		String bean = AtributoUtil.getBean(raiz);
		Mapa mapaDAO = AtributoUtil.getMapaDAO(raiz);
		Mapa mapaService = AtributoUtil.getMapaService(raiz);

		if (mapaDAO == null || mapaService == null) {
			return;
		}

		Classe classe = new Classe(bean + " implements " + AtributoUtil.getComponente(mapaService));
		arquivo.add(classe);

		classe.add(new Anotacao("Inject", null, true));
		Campo service = new Campo(AtributoUtil.getTipoDAO(mapaDAO));
		classe.add(service).ql();

		Parametros params = new Parametros(AtributoUtil.getTipoFilter(raiz));
		Funcao funcao = new Funcao(AtributoConstantes.PUBLIC, AtributoUtil.getListDTO(raiz),
				AtributoUtil.getPesquisar(mapaService), params);
		funcao.addReturn("dao." + AtributoUtil.getPesquisarFilter(mapaDAO));
		classe.add(new Anotacao(AtributoConstantes.OVERRIDE, null, true));
		classe.add(funcao).ql();

		params = new Parametros(new Tipo("List<" + AtributoUtil.getDTO(raiz) + ">", "dtos"));
		funcao = new Funcao(AtributoConstantes.PUBLIC, "byte[]", AtributoUtil.getExportar(mapaService), params);
		funcao.addReturn("new byte[0]");
		classe.add(new Anotacao(AtributoConstantes.OVERRIDE, null, true));
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
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.add(AtributoConstantes.IMPORT_LIST).ql();
		}

		Mapa mapaDAO = AtributoUtil.getMapaDAO(raiz);

		if (mapaDAO == null) {
			return;
		}

		Interface interfac = new Interface(AtributoUtil.getComponente(mapaDAO));
		arquivo.add(interfac);

		Parametros params = new Parametros(AtributoUtil.getTipoFilter(raiz));
		FuncaoInter funcao = new FuncaoInter(AtributoUtil.getListDTO(raiz), AtributoUtil.getPesquisar(mapaDAO), params);
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
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.addImport("java.util.ArrayList");
			arquivo.add(AtributoConstantes.IMPORT_LIST).ql();
			arquivo.addImport("javax.persistence.EntityManager");
			arquivo.addImport("javax.persistence.PersistenceContext").ql();
		}

		String daoImpl = AtributoUtil.getDAOImpl(raiz);
		Mapa mapaDAO = AtributoUtil.getMapaDAO(raiz);

		if (mapaDAO == null) {
			return;
		}

		Classe classe = new Classe(daoImpl + " implements " + AtributoUtil.getComponente(mapaDAO));
		arquivo.add(classe);

		classe.add(new Anotacao("PersistenceContext", "unitName = " + Util.citar2("nomeUnit"), true));
		Campo entityManager = new Campo(new Tipo("EntityManager", "entityManager"));
		classe.add(entityManager).ql();

		Parametros params = new Parametros(AtributoUtil.getTipoFilter(raiz));
		Funcao funcao = new Funcao(AtributoConstantes.PUBLIC, AtributoUtil.getListDTO(raiz),
				AtributoUtil.getPesquisar(mapaDAO), params);
		funcao.addInstrucao(AtributoUtil.getListDTO(raiz) + " resp = new ArrayList<>()");
		funcao.addComentario("entityManager.find...").ql();
		funcao.addReturn("resp");
		classe.add(new Anotacao(AtributoConstantes.OVERRIDE, null, true));
		classe.add(funcao);

		arquivo.gerar(0, pool);
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
	void gerar(Mapa raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.addImport("org.junit.Test");
			arquivo.addImport("org.junit.runner.RunWith");
			arquivo.addImport("org.mockito.InjectMocks");
			arquivo.addImport("org.mockito.Mock");
			arquivo.addImport("org.mockito.junit.MockitoJUnitRunner").ql();

			arquivo.add(new Anotacao("RunWith", "MockitoJUnitRunner.class", true));
		}

		Mapa mapaService = AtributoUtil.getMapaService(raiz);
		Mapa mapaTest = AtributoUtil.getMapaTest(raiz);

		if (mapaService == null || mapaTest == null) {
			return;
		}

		Classe classe = new Classe(AtributoUtil.getComponente(mapaTest));
		arquivo.add(classe);

		classe.add(new Anotacao("InjectMocks", null, true));
		Campo service = new Campo(new Tipo(AtributoUtil.getComponente(mapaService), "service"));
		classe.add(service).ql();

		Funcao funcaoPesquisar = new Funcao(AtributoConstantes.PUBLIC, "void", AtributoUtil.getPesquisar(mapaTest),
				new Parametros());
		funcaoPesquisar.addComentario("...");

		classe.add(new Anotacao("Test", null, true));
		classe.add(funcaoPesquisar).ql();

		Funcao funcaoExportar = new Funcao(AtributoConstantes.PUBLIC, "void", AtributoUtil.getExportar(mapaTest),
				new Parametros());
		funcaoExportar.addComentario("...");

		classe.add(new Anotacao("Test", null, true));
		classe.add(funcaoExportar);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}
}