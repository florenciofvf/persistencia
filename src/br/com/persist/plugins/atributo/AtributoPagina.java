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
import br.com.persist.geradores.Anotacao;
import br.com.persist.geradores.AnotacaoPath;
import br.com.persist.geradores.Arquivo;
import br.com.persist.geradores.CampoPrivado;
import br.com.persist.geradores.ClassePublica;
import br.com.persist.geradores.Container;
import br.com.persist.geradores.Else;
import br.com.persist.geradores.Espaco;
import br.com.persist.geradores.Funcao;
import br.com.persist.geradores.FuncaoAbstrata;
import br.com.persist.geradores.FuncaoJS;
import br.com.persist.geradores.FuncaoPublica;
import br.com.persist.geradores.If;
import br.com.persist.geradores.InterfacePublica;
import br.com.persist.geradores.InvocaProm;
import br.com.persist.geradores.MetodoGet;
import br.com.persist.geradores.MetodoSet;
import br.com.persist.geradores.Parametros;
import br.com.persist.geradores.ReturnJS;
import br.com.persist.geradores.VarJS;
import br.com.persist.geradores.VarObjJS;
import br.com.persist.geradores.Variavel;

public class AtributoPagina extends Panel {
	private static final long serialVersionUID = 1L;
	private final PainelAtributo painelAtributo;
	private final PainelFichario painelFichario;
	private transient Raiz raiz;

	public AtributoPagina(File file) {
		painelAtributo = new PainelAtributo(file);
		painelFichario = new PainelFichario(this);
		montarLayout();
		abrir();
	}

	public Raiz getRaiz() {
		if (raiz == null) {
			raiz = new Raiz();
		}
		return raiz;
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
						raiz = new Raiz(handler.getRaiz());
						if (raiz.getMapaAtributos() != null) {
							Mapa mapAtributos = raiz.getMapaAtributos();
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
			gerar(pagina.getRaiz(), lista);
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

	abstract void gerar(Raiz raiz, List<Atributo> atributos);

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
	void gerar(Raiz raiz, List<Atributo> atributos) {
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

		Mapa mapaControllerJS = raiz.getMapaControllerJS();

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
	protected transient FuncaoJS funcaoController;

	PainelControllerJS(AtributoPagina pagina) {
		super(pagina);
	}

	@Override
	String getChaveTitulo() {
		return "label.controller_js";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		String filtro = raiz.getFiltro();

		Mapa mapaControllerJS = raiz.getMapaControllerJS();
		Mapa mapaServiceJS = raiz.getMapaServiceJS();

		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		Arquivo arquivo = criarArquivo(mapaControllerJS, mapaServiceJS);
		FuncaoJS funcao = funcaoController;
		funcao.addInstrucao("var vm = this").newLine();
		funcao.addInstrucao("vm.pesquisados = new NgTableParams()");
		funcao.addInstrucao("vm." + filtro + " = {}").newLine();

		funcao.add(fnLimparFiltro(mapaControllerJS, filtro)).newLine();
		funcao.add(fnPesquisa(mapaControllerJS, mapaServiceJS, filtro)).newLine();
		funcao.add(fnPDF(mapaControllerJS, mapaServiceJS, filtro)).newLine();
		funcao.add(fnProcessarFile());

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	protected Arquivo criarArquivo(Mapa mapaControllerJS, Mapa mapaServiceJS) {
		Arquivo arquivo = new Arquivo();
		arquivo.addInstrucao(
				AtributoUtil.getComponente(mapaControllerJS) + ".$inject = ['$scope', '$state', 'NgTableParams', '"
						+ AtributoUtil.getComponente(mapaServiceJS) + "']")
				.newLine();

		String string = ", ";
		Parametros params = new Parametros("$scope");
		params.addString(string);
		params.addVarJS("$state").addString(string);
		params.addVarJS("NgTableParams").addString(string);
		params.addVarJS(AtributoUtil.getComponente(mapaServiceJS));
		funcaoController = arquivo
				.criarFuncaoJS(AtributoConstantes.FUNCTION + AtributoUtil.getComponente(mapaControllerJS), params);

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
		params.addVarJS("file").append(", ").addVarJS("arquivo");
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
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		String filtro = raiz.getFiltro();

		Mapa mapaControllerJS = raiz.getMapaControllerJS();
		Mapa mapaServiceJS = raiz.getMapaServiceJS();

		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		Arquivo arquivo = criarArquivo(mapaControllerJS, mapaServiceJS);
		FuncaoJS funcao = funcaoController;
		funcao.add(fnParam(filtro, atributos));

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private Container fnParam(String filtro, List<Atributo> atributos) {
		FuncaoJS funcao = new FuncaoJS("function criarParam" + Util.capitalize(filtro), new Parametros());
		funcao.add(objParam(filtro, atributos)).newLine();
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
			obj.newLine();
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
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		String filtro = raiz.getFiltro();

		Mapa mapaControllerJS = raiz.getMapaControllerJS();
		Mapa mapaServiceJS = raiz.getMapaServiceJS();

		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		Arquivo arquivo = criarArquivo(mapaControllerJS, mapaServiceJS);
		FuncaoJS funcao = funcaoController;

		funcao.add(fnGetTime()).newLine();
		funcao.add(fnValidar(filtro, atributos));

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private Container fnGetTime() {
		FuncaoJS funcao = new FuncaoJS("function getTime", new Parametros(new VarJS("obj")));
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
			funcao.add(ifVazios(filtro, atributos)).newLine();
		}
		for (int i = 0; i < atributos.size(); i++) {
			Atributo att = atributos.get(i);
			funcao.add(ifObrigatorio(filtro, att));
			if (i + 1 < atributos.size()) {
				funcao.newLine();
			}
		}
		if (!atributos.isEmpty()) {
			funcao.newLine();
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
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Mapa mapaServiceJS = raiz.getMapaServiceJS();
		Mapa mapaRest = raiz.getMapaRest();

		if (mapaServiceJS == null || mapaRest == null) {
			return;
		}

		Arquivo arquivo = new Arquivo();
		arquivo.addInstrucao(AtributoUtil.getComponente(mapaServiceJS) + ".$inject = ['Restangular']").newLine();

		Parametros params = new Parametros(new VarJS("Restangular"));
		FuncaoJS funcao = arquivo.criarFuncaoJS(AtributoConstantes.FUNCTION + AtributoUtil.getComponente(mapaServiceJS),
				params);

		funcao.addInstrucao("var PATH = '" + mapaRest.getString(AtributoConstantes.END_POINT) + "'").newLine();
		ReturnJS returnJS = new ReturnJS();
		funcao.add(returnJS);

		returnJS.add(fnPesquisar(mapaServiceJS, mapaRest));
		returnJS.add(fnGerarPDF(mapaServiceJS, mapaRest));

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private Container fnPesquisar(Mapa mapaServiceJS, Mapa mapaRest) {
		Parametros params = new Parametros(new VarJS(AtributoConstantes.FILTRO));
		FuncaoJS funcao = new FuncaoJS(AtributoUtil.getPesquisar(mapaServiceJS) + AtributoConstantes.FUNCTION3, params);
		funcao.addReturn("Restangular.all(PATH).customGET('" + AtributoUtil.getPesquisar(mapaRest) + "', filtro)");
		return funcao;
	}

	private Container fnGerarPDF(Mapa mapaServiceJS, Mapa mapaRest) {
		Parametros params = new Parametros(new VarJS(AtributoConstantes.FILTRO));
		FuncaoJS funcao = new FuncaoJS(AtributoUtil.getExportar(mapaServiceJS) + AtributoConstantes.FUNCTION3, params);
		funcao.setStrFinal("");
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
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		ClassePublica classe = arquivo.criarClassePublica(raiz.getDTO());

		for (Atributo att : atributos) {
			classe.addCampoPrivado(att.criarVariavel());
		}

		for (Atributo att : atributos) {
			Variavel tipo = att.criarVariavel();
			classe.criarMetodoGet(tipo);
			classe.newLine();
			classe.criarMetodoSet(tipo);
			classe.newLine();
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
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.addImport("javax.ws.rs.QueryParam").newLine();
		}

		ClassePublica classe = arquivo.criarClassePublica(raiz.getFilter());

		int i = 0;
		for (Atributo att : atributos) {
			if (i++ > 0) {
				classe.newLine();
			}
			classe.addAnotacao("QueryParam(" + Util.citar2(att.getNome()) + ")");
			classe.addCampoPrivado(att.criarVariavel());
		}

		for (Atributo att : atributos) {
			Variavel tipo = att.criarVariavel();
			classe.criarMetodoGet(tipo);
			classe.newLine();
			classe.criarMetodoSet(tipo);
			classe.newLine();
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
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Mapa mapaService = raiz.getMapaService();
		Mapa mapaRest = raiz.getMapaRest();

		if (mapaService == null || mapaRest == null) {
			return;
		}

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.addImport(AtributoConstantes.IMPORT_LIST).newLine();
			arquivo.addImport("javax.inject.Inject").newLine();
			arquivo.addImport("javax.ws.rs.Consumes");
			arquivo.addImport("javax.ws.rs.Produces").newLine();
			arquivo.addImport("javax.ws.rs.core.MediaType").newLine();
			arquivo.addImport("javax.ws.rs.BeanParam");
			arquivo.addImport("javax.ws.rs.GET");
			arquivo.addImport("javax.ws.rs.Path");
			arquivo.addImport("javax.ws.rs.Produces");
			arquivo.addImport("javax.ws.rs.QueryParam");
			arquivo.addImport("javax.ws.rs.core.MediaType").newLine();
			arquivo.addComentario("br.gov.dpf.framework.seguranca.RestSeguranca;").newLine();

			arquivo.addAnotacaoPath(Util.citar2(mapaRest.getString(AtributoConstantes.END_POINT)));
		}

		ClassePublica classe = arquivo
				.criarClassePublica(AtributoUtil.getComponente(mapaRest) + " extends ApplicationRest");

		injetar(classe, new Variavel(AtributoUtil.getComponente(mapaService), "service")).newLine();
		injetar(classe, new Variavel(AtributoUtil.getComponente(mapaService) + "PDF", "servicePDF")).newLine();
		criarGetListaDTO(raiz, mapaRest, mapaService, classe).newLine();
		criarGetGerarPDF(raiz, mapaRest, mapaService, classe);

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}

	private ClassePublica injetar(ClassePublica classe, Variavel tipo) {
		classe.addAnotacao("Inject");
		classe.addCampoPrivado(tipo);
		return classe;
	}

	private ClassePublica criarGetListaDTO(Raiz raiz, Mapa mapaRest, Mapa mapaService, ClassePublica classe) {
		classe.addAnotacao("GET");
		classe.addAnotacaoPath(Util.citar2(AtributoUtil.getPesquisar(mapaRest)));
		classe.addAnotacao("Consumes(" + AtributoConstantes.APPLICATION_JSON + ")");
		classe.addAnotacao("Produces(" + AtributoConstantes.APPLICATION_JSON + ")");

		FuncaoPublica funcao = classe.criarFuncaoPublica(raiz.getListDTO(), AtributoUtil.getPesquisar(mapaRest),
				beanParam(raiz));
		funcao.addReturn("service." + AtributoUtil.getPesquisarFilter(mapaService));

		return classe;
	}

	private ClassePublica criarGetGerarPDF(Raiz raiz, Mapa mapaRest, Mapa mapaService, ClassePublica classe) {
		classe.addAnotacao("GET");
		classe.addAnotacaoPath(Util.citar2(AtributoUtil.getExportar(mapaRest)));
		classe.addAnotacao("Consumes(" + AtributoConstantes.APPLICATION_JSON + ")");
		classe.addAnotacao("Produces(" + "{MediaType.APPLICATION_OCTET_STREAM}" + ")");

		FuncaoPublica funcao = classe.criarFuncaoPublica("Response", AtributoUtil.getExportar(mapaRest),
				beanParam(raiz));
		funcao.addInstrucao(raiz.getListDTO() + " dtos = service." + AtributoUtil.getPesquisarFilter(mapaService));
		funcao.addInstrucao("byte[] bytes = servicePDF." + AtributoUtil.getExportar(mapaService) + "(dtos)").newLine();
		funcao.addInstrucao("ResponseBuilder response = Response.ok(bytes)");
		funcao.addInstrucao("response.header(\"Content-Disposition\", \"attachment;filename=arquivo.pdf\")");
		funcao.addInstrucao("response.header(\"Content-type\", MediaType.APPLICATION_OCTET_STREAM)");
		funcao.addReturn("response.build()");

		return classe;
	}

	private Parametros beanParam(Raiz raiz) {
		Parametros params = new Parametros("BeanParam");
		params.addEspaco();
		params.addVariavel(raiz.getTipoFilter());
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
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.addImport(AtributoConstantes.IMPORT_LIST).newLine();
			arquivo.addImport("javax.ejb.Local").newLine();

			arquivo.addAnotacao("Local");
		}

		Mapa mapaService = raiz.getMapaService();

		if (mapaService == null) {
			return;
		}

		InterfacePublica interfacee = arquivo.criarInterfacePublica(AtributoUtil.getComponente(mapaService));

		interfacee.criarFuncaoAbstrata(raiz.getListDTO(), AtributoUtil.getPesquisar(mapaService),
				new Parametros(raiz.getTipoFilter()));
		interfacee.newLine();

		interfacee.criarFuncaoAbstrata("byte[]", AtributoUtil.getExportar(mapaService),
				new Parametros(new Variavel(raiz.getListDTO(), "dtos")));

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
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.addImport(AtributoConstantes.IMPORT_LIST).newLine();
			arquivo.addImport("javax.ejb.LocalBean");
			arquivo.addImport("javax.ejb.Stateless");
			arquivo.addImport("javax.ejb.TransactionManagement");
			arquivo.addImport("javax.ejb.TransactionManagementType").newLine();

			arquivo.addAnotacao("Stateless");
			arquivo.addAnotacao("LocalBean");
			arquivo.addAnotacao("TransactionManagement(TransactionManagementType.CONTAINER)");
		}

		String bean = raiz.getBean();
		Mapa mapaDAO = raiz.getMapaDAO();
		Mapa mapaService = raiz.getMapaService();

		if (mapaDAO == null || mapaService == null) {
			return;
		}

		ClassePublica classe = arquivo
				.criarClassePublica(bean + " implements " + AtributoUtil.getComponente(mapaService));

		classe.addAnotacao("Inject");
		classe.addCampoPrivado(raiz.getTipoDAO()).newLine();

		classe.addOverride();
		Funcao funcao = classe.criarFuncaoPublica(raiz.getListDTO(), AtributoUtil.getPesquisar(mapaService),
				new Parametros(raiz.getTipoFilter()));
		funcao.addReturn("dao." + AtributoUtil.getPesquisarFilter(mapaDAO));
		classe.newLine();

		classe.addOverride();
		funcao = classe.criarFuncaoPublica("byte[]", AtributoUtil.getExportar(mapaService),
				new Parametros(new Variavel(raiz.getListDTO(), "dtos")));
		funcao.addReturn("new byte[0]");

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
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.addImport(AtributoConstantes.IMPORT_LIST).newLine();
		}

		Mapa mapaDAO = raiz.getMapaDAO();

		if (mapaDAO == null) {
			return;
		}

		InterfacePublica interfacee = arquivo.criarInterfacePublica(AtributoUtil.getComponente(mapaDAO));

		interfacee.criarFuncaoAbstrata(raiz.getListDTO(), AtributoUtil.getPesquisar(mapaDAO),
				new Parametros(raiz.getTipoFilter()));

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
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.addImport("java.util.ArrayList");
			arquivo.addImport(AtributoConstantes.IMPORT_LIST).newLine();
			arquivo.addImport("javax.persistence.EntityManager");
			arquivo.addImport("javax.persistence.PersistenceContext").newLine();
		}

		String daoImpl = raiz.getDAOImpl();
		Mapa mapaDAO = raiz.getMapaDAO();

		if (mapaDAO == null) {
			return;
		}

		ClassePublica classe = arquivo
				.criarClassePublica(daoImpl + " implements " + AtributoUtil.getComponente(mapaDAO));

		classe.addAnotacao("PersistenceContext(" + "unitName = " + Util.citar2("nomeUnit") + ")");
		classe.addCampoPrivado(new Variavel("EntityManager", "entityManager"));
		classe.newLine();

		classe.addOverride();
		Funcao funcao = classe.criarFuncaoPublica(raiz.getListDTO(), AtributoUtil.getPesquisar(mapaDAO),
				new Parametros(raiz.getTipoFilter()));
		funcao.addInstrucao(raiz.getListDTO() + " resp = new ArrayList<>()");
		funcao.addComentario("entityManager.find...").newLine();
		funcao.addReturn("resp");

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
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (!atributos.isEmpty()) {
			arquivo.addImport("org.junit.Test");
			arquivo.addImport("org.junit.runner.RunWith");
			arquivo.addImport("org.mockito.InjectMocks");
			arquivo.addImport("org.mockito.Mock");
			arquivo.addImport("org.mockito.junit.MockitoJUnitRunner").newLine();

			arquivo.addAnotacao("RunWith(MockitoJUnitRunner.class");
		}

		Mapa mapaService = raiz.getMapaService();
		Mapa mapaTest = raiz.getMapaTest();

		if (mapaService == null || mapaTest == null) {
			return;
		}

		ClassePublica classe = arquivo.criarClassePublica(AtributoUtil.getComponente(mapaTest));

		classe.addAnotacao("InjectMocks");
		classe.addCampoPrivado(new Variavel(AtributoUtil.getComponente(mapaService), "service")).newLine();

		classe.addAnotacao("Test");
		Funcao funcaoPesquisar = classe.criarFuncaoPublica("void", AtributoUtil.getPesquisar(mapaTest));
		funcaoPesquisar.addComentario("...");

		classe.newLine();

		classe.addAnotacao("Test");
		Funcao funcaoExportar = classe.criarFuncaoPublica("void", AtributoUtil.getExportar(mapaTest));
		funcaoExportar.addComentario("...");

		arquivo.gerar(0, pool);
		setText(pool.toString());
	}
}