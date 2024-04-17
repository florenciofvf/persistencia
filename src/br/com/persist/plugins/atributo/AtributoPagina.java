package br.com.persist.plugins.atributo;

import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Component;
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
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.geradores.Arquivo;
import br.com.persist.geradores.ClassePublica;
import br.com.persist.geradores.Container;
import br.com.persist.geradores.Else;
import br.com.persist.geradores.Funcao;
import br.com.persist.geradores.FuncaoPublica;
import br.com.persist.geradores.If;
import br.com.persist.geradores.InterfacePublica;
import br.com.persist.geradores.JSArquivo;
import br.com.persist.geradores.JSFuncao;
import br.com.persist.geradores.JSFuncaoAtributo;
import br.com.persist.geradores.JSFuncaoPropriedade;
import br.com.persist.geradores.JSInvocaProm;
import br.com.persist.geradores.JSReturnObj;
import br.com.persist.geradores.JSVar;
import br.com.persist.geradores.JSVarObj;
import br.com.persist.geradores.Parametros;
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
					StringBuilder sb = new StringBuilder();
					for (ChaveValor cv : mapaHierarquia.getLista()) {
						if (sb.length() > 0) {
							sb.append("\n\n");
						}
						Object valor = cv.getValor();
						String toStr = (valor instanceof String) ? Util.citar2(valor.toString())
								: ((Mapa) valor).toString(1);
						sb.append(Mapa.tabular(1) + Util.citar2(cv.getChave()) + ": " + toStr);
					}
					textArea.setText("{\n" + sb.toString() + "\n}");
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
						painelFichario.selecionarModeloLista(raiz.isModeloLista());
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
				resp.put(AtributoConstantes.MODELO_LISTA, "true");
				resp.put(AtributoConstantes.ATRIBUTOS, criarMapaAtributos(atributos));
				resp.put(AtributoConstantes.FILTRO_JS, AtributoConstantes.FILTRO);
				resp.put(AtributoConstantes.CONTROLLER_JS, criarMapa(AtributoConstantes.CONTROLLER_JS,
						new ChaveValor(AtributoConstantes.LIMPAR_FILTRO, AtributoConstantes.LIMPAR_FILTRO)));
				resp.put(AtributoConstantes.SERVICE_JS, criarMapa(AtributoConstantes.SERVICE_JS));
				resp.put(AtributoConstantes.FILTER, Util.capitalize(AtributoConstantes.FILTER));
				resp.put(AtributoConstantes.REST, criarMapa(AtributoConstantes.REST,
						new ChaveValor(AtributoConstantes.END_POINT, AtributoConstantes.END_POINT)));
				resp.put(AtributoConstantes.DTO, AtributoConstantes.DTO.toUpperCase());
				resp.put(AtributoConstantes.SERVICE, criarMapa(AtributoConstantes.SERVICE));
				resp.put(AtributoConstantes.BEAN, Util.capitalize(AtributoConstantes.BEAN));
				resp.put(AtributoConstantes.DAO, criarMapa(AtributoConstantes.DAO.toUpperCase()));
				resp.put(AtributoConstantes.DAO_IMPL, AtributoConstantes.DAO_IMP2);
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
		addAba(new PainelValidarJS(pagina));
		addAba(new PainelParamJS(pagina));
		addAba(new PainelControllerJS(pagina));
		addAba(new PainelServiceJS(pagina));
		addAba(new PainelFilter(pagina));
		addAba(new PainelRest(pagina));
		addAba(new PainelDTO(pagina));
		addAba(new PainelService(pagina));
		addAba(new PainelBean(pagina));
		addAba(new PainelDAO(pagina));
		addAba(new PainelDAOImpl(pagina));
		addAba(new PainelTest(pagina));
	}

	private void addAba(AbstratoPanel panel) {
		addTab(AtributoMensagens.getString(panel.getChaveTitulo()), panel);
	}

	void selecionarModeloLista(boolean b) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof AbstratoPanel) {
				AbstratoPanel p = (AbstratoPanel) cmp;
				p.selecionarModeloLista(b);
			}
		}
	}
}

abstract class AbstratoPanel extends Panel {
	protected final CheckBox chkModeloLista = new CheckBox(AtributoMensagens.getString("label.modelo_lista"), false);
	private static final long serialVersionUID = 1L;
	protected final JTextPane textArea = new JTextPane();
	private final Toolbar toolbar = new Toolbar();
	private final AtributoPagina pagina;

	AbstratoPanel(AtributoPagina pagina, boolean comCheckModelo) {
		this.pagina = pagina;
		montarLayout(comCheckModelo);
	}

	public AtributoPagina getPagina() {
		return pagina;
	}

	private void montarLayout(boolean comCheckModelo) {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, criarPanelTextArea());
		if (comCheckModelo) {
			toolbar.add(chkModeloLista);
		}
	}

	void selecionarModeloLista(boolean b) {
		chkModeloLista.setSelected(b);
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
		super(pagina, false);
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

abstract class AbstratoPainelJS extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	AbstratoPainelJS(AtributoPagina pagina, boolean comCheckModelo) {
		super(pagina, comCheckModelo);
	}

	protected JSArquivo criarArquivo(Mapa mapaControllerJS, Mapa mapaServiceJS) {
		JSArquivo arquivo = new JSArquivo();
		arquivo.addInstrucao(
				AtributoUtil.getComponente(mapaControllerJS) + ".$inject = ['$scope', '$state', 'NgTableParams', '"
						+ AtributoUtil.getComponente(mapaServiceJS) + "']")
				.newLine();
		return arquivo;
	}

	protected JSFuncao criarFuncao(JSArquivo arquivo, Mapa mapaControllerJS, Mapa mapaServiceJS) {
		final String string = ", ";
		Parametros params = new Parametros("$scope");
		params.addString(string).addString("$state").addString(string).addString("NgTableParams").addString(string)
				.addString(AtributoUtil.getComponente(mapaServiceJS));
		return arquivo.criarJSFuncao(AtributoUtil.getComponente(mapaControllerJS), params);
	}
}

class PainelControllerJS extends AbstratoPainelJS {
	private static final long serialVersionUID = 1L;

	PainelControllerJS(AtributoPagina pagina) {
		super(pagina, true);
	}

	@Override
	String getChaveTitulo() {
		return "label.controller_js";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		String filtro = raiz.getFiltroJS();

		Mapa mapaControllerJS = raiz.getMapaControllerJS();
		Mapa mapaServiceJS = raiz.getMapaServiceJS();

		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		JSArquivo arquivo = criarArquivo(mapaControllerJS, mapaServiceJS);
		JSFuncao funcao = criarFuncao(arquivo, mapaControllerJS, mapaServiceJS);
		funcao.addInstrucao("var vm = this").newLine();
		funcao.addInstrucao("vm.pesquisados = new NgTableParams()");
		funcao.addInstrucao("vm." + filtro + " = {}").newLine();

		fnLimparFiltro(funcao, mapaControllerJS, filtro);
		funcao.newLine();
		fnPesquisa(funcao, mapaControllerJS, mapaServiceJS, filtro);
		funcao.newLine();
		fnExportar(funcao, mapaControllerJS, mapaServiceJS, filtro);
		funcao.newLine();
		fnProcessarFile(funcao);

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}

	private void fnLimparFiltro(JSFuncao funcao, Mapa mapaControllerJS, String filtro) {
		JSFuncaoAtributo limpar = funcao
				.criarJSFuncaoAtributo("vm." + mapaControllerJS.getString(AtributoConstantes.LIMPAR_FILTRO));
		limpar.addInstrucao("vm." + filtro + " = {}");
	}

	private void fnPesquisa(JSFuncao funcao, Mapa mapaControllerJS, Mapa mapaServiceJS, String filtro) {
		JSFuncaoAtributo pesquisar = funcao.criarJSFuncaoAtributo("vm." + AtributoUtil.getPesquisar(mapaControllerJS));
		pesquisar.addInstrucao("var msg = validar" + Util.capitalize(filtro) + "()");

		Else elsee = new Else();
		elsee.addComentario("MensagemService.error(msg);");
		elsee.addComentario("$scope.$emit('msg', msg, null, 'warning');");

		If iff = pesquisar.criarIf("isVazio(msg)", elsee);

		JSInvocaProm invocaProm = iff.criarJSInvocaProm(
				AtributoUtil.getComponente(mapaServiceJS) + "." + AtributoUtil.getPesquisar(mapaServiceJS)
						+ "(criarParam" + Util.capitalize(filtro) + "()).then(function(result) {");

		if (chkModeloLista.isSelected()) {
			invocaProm.addInstrucao("var lista = result.data");
			invocaProm.addComentario("vm.pesquisados.settings().dataset = lista;");
			invocaProm.addInstrucao("vm.pesquisados.settings({data: lista})");
			invocaProm.addInstrucao("vm.pesquisados.reload()");

			If ifLength = invocaProm.criarIf("lista.length === 0", null);
			ifLength.addComentario("MensagemService.info('Nenhum registro encontrado');");
			ifLength.addComentario("$scope.$emit('msg', 'Nenhum registro encontrado', null, 'warning');");
		} else {
			Else elseData = new Else();
			elseData.addInstrucao("console.log('Sem data')");
			If ifData = invocaProm.criarIf("result.data", elseData);
			ifData.addInstrucao("var dto = result.data.plain()");
		}
	}

	private void fnExportar(JSFuncao funcao, Mapa mapaControllerJS, Mapa mapaServiceJS, String filtro) {
		JSFuncaoAtributo exportar = funcao.criarJSFuncaoAtributo("vm." + AtributoUtil.getExportar(mapaControllerJS));
		exportar.addInstrucao("var msg = validar" + Util.capitalize(filtro) + "()");

		Else elsee = new Else();
		elsee.addComentario("MensagemService.error(msg);");
		elsee.addComentario("$scope.$emit('msg', msg, null, 'warning');");

		If iff = exportar.criarIf("isVazio(msg)", elsee);

		JSInvocaProm invocaProm = iff.criarJSInvocaProm(
				AtributoUtil.getComponente(mapaServiceJS) + "." + AtributoUtil.getExportar(mapaServiceJS)
						+ "(criarParam" + Util.capitalize(filtro) + "()).then(function(result) {");

		invocaProm.addComentario("var file = new Blob([result.data], {type: 'application/octet-stream'});");
		invocaProm.addComentario("processarFile(file, \"arquivo.xls\");");

		invocaProm.addInstrucao("var file = new Blob([result.data], {type: 'application/pdf'})");
		invocaProm.addInstrucao("processarFile(file, \"arquivo.pdf\")");
	}

	private Container fnProcessarFile(JSFuncao funcao) {
		Parametros params = new Parametros("file");
		params.addString(", ").addString("arquivo");
		JSFuncao processar = funcao.criarJSFuncao("processarFile", params);
		processar.addInstrucao("var downloadLink = angular.element('<a></a>')");
		processar.addInstrucao("downloadLink.attr('href', window.URL.createObjectURL(file))");
		processar.addInstrucao("downloadLink.attr('download', arquivo)");
		processar.addInstrucao("var link = downloadLink[0]");
		processar.addInstrucao("document.body.appendChild(link)");
		processar.addInstrucao("link.click()");
		processar.addInstrucao("document.body.removeChild(link)");
		return processar;
	}
}

class PainelParamJS extends AbstratoPainelJS {
	private static final long serialVersionUID = 1L;

	PainelParamJS(AtributoPagina pagina) {
		super(pagina, false);
	}

	@Override
	String getChaveTitulo() {
		return "label.param_js";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		String filtro = raiz.getFiltroJS();

		Mapa mapaControllerJS = raiz.getMapaControllerJS();
		Mapa mapaServiceJS = raiz.getMapaServiceJS();

		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		JSArquivo arquivo = criarArquivo(mapaControllerJS, mapaServiceJS);
		JSFuncao funcao = criarFuncao(arquivo, mapaControllerJS, mapaServiceJS);
		fnParam(funcao, filtro, atributos);

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}

	private void fnParam(JSFuncao funcao, String filtro, List<Atributo> atributos) {
		JSFuncao criarParam = funcao.criarJSFuncao("criarParam" + Util.capitalize(filtro));
		objParam(criarParam, filtro, atributos);
		criarParam.newLine();
		criarParam.addReturn("param");
	}

	private void objParam(JSFuncao funcao, String filtro, List<Atributo> atributos) {
		JSVarObj obj = funcao.criarJSVarObj("param");
		for (int i = 0; i < atributos.size(); i++) {
			Atributo att = atributos.get(i);
			obj.addJSAtributo(i + 1 < atributos.size(), att.getNome(), att.gerarViewToBack(filtro));
		}
	}
}

class PainelValidarJS extends AbstratoPainelJS {
	private static final long serialVersionUID = 1L;

	PainelValidarJS(AtributoPagina pagina) {
		super(pagina, false);
	}

	@Override
	String getChaveTitulo() {
		return "label.validar_js";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		String filtro = raiz.getFiltroJS();

		Mapa mapaControllerJS = raiz.getMapaControllerJS();
		Mapa mapaServiceJS = raiz.getMapaServiceJS();

		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		JSArquivo arquivo = criarArquivo(mapaControllerJS, mapaServiceJS);
		JSFuncao funcao = criarFuncao(arquivo, mapaControllerJS, mapaServiceJS);

		fnGetTime(funcao);
		funcao.newLine();
		fnValidar(funcao, filtro, atributos);

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}

	private void fnGetTime(JSFuncao funcao) {
		JSFuncao getTime = funcao.criarJSFuncao("getTime", new Parametros(new JSVar("obj")));
		If iff = getTime.criarIf("obj instanceof Date", null);
		iff.addReturn("obj.getTime()");
		getTime.addReturn("null");
	}

	private void fnValidar(JSFuncao funcao, String filtro, List<Atributo> atributos) {
		JSFuncao validar = funcao.criarJSFuncao("validar" + Util.capitalize(filtro));
		validar.addComentario("$scope.$emit('msgClear');");
		if (atributos.size() > 1) {
			ifVazios(validar, filtro, atributos);
			validar.newLine();
		}
		for (int i = 0; i < atributos.size(); i++) {
			Atributo att = atributos.get(i);
			ifObrigatorio(validar, filtro, att);
			if (i + 1 < atributos.size()) {
				validar.newLine();
			}
		}
		if (!atributos.isEmpty()) {
			validar.newLine();
		}
		validar.addReturn("null");
	}

	private void ifVazios(JSFuncao funcao, String filtro, List<Atributo> atributos) {
		If iff = funcao.criarIf(vazios(filtro, atributos), null);
		iff.addReturn("'Favor preencher pelo ao menos um campo de pesquisa'");
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

	private void ifObrigatorio(JSFuncao funcao, String filtro, Atributo att) {
		If iff = funcao.criarIf(att.gerarIsVazioJS(filtro), null);
		String campo = Util.isEmpty(att.getRotulo()) ? att.getNome() : att.getRotulo();
		iff.addReturn("'Campo " + campo + " Obrigat\u00F3rio.'");
	}
}

class PainelServiceJS extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelServiceJS(AtributoPagina pagina) {
		super(pagina, false);
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

		JSArquivo arquivo = new JSArquivo();
		arquivo.addInstrucao(AtributoUtil.getComponente(mapaServiceJS) + ".$inject = ['Restangular']").newLine();

		Parametros params = new Parametros(new JSVar("Restangular"));
		JSFuncao funcao = arquivo.criarJSFuncao(AtributoUtil.getComponente(mapaServiceJS), params);

		funcao.addInstrucao("var PATH = '" + mapaRest.getString(AtributoConstantes.END_POINT) + "'").newLine();
		JSReturnObj returnObj = funcao.criarJSReturnObj();

		fnPesquisar(returnObj, mapaServiceJS, mapaRest);
		fnGerarPDF(returnObj, mapaServiceJS, mapaRest);

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}

	private void fnPesquisar(JSReturnObj returnObj, Mapa mapaServiceJS, Mapa mapaRest) {
		Parametros params = new Parametros(new JSVar(AtributoConstantes.FILTRO));
		JSFuncaoPropriedade funcao = returnObj.criarJSFuncaoPropriedade(true, AtributoUtil.getPesquisar(mapaServiceJS),
				params);
		funcao.addReturn("Restangular.all(PATH).customGET('" + AtributoUtil.getPesquisar(mapaRest) + "', filtro)");
	}

	private void fnGerarPDF(JSReturnObj returnObj, Mapa mapaServiceJS, Mapa mapaRest) {
		Parametros params = new Parametros(new JSVar(AtributoConstantes.FILTRO));
		JSFuncaoPropriedade funcao = returnObj.criarJSFuncaoPropriedade(false, AtributoUtil.getExportar(mapaServiceJS),
				params);
		funcao.addReturn("Restangular.all(PATH).withHttpConfig({responseType: \"arraybuffer\"}).customGET('"
				+ AtributoUtil.getExportar(mapaRest) + "', filtro)");
	}
}

class PainelDTO extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelDTO(AtributoPagina pagina) {
		super(pagina, false);
	}

	@Override
	String getChaveTitulo() {
		return "label.dto";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		if (AtributoUtil.contemParseDateValido(atributos)) {
			arquivo.addImport("java.util.Date").newLine();
		}
		ClassePublica classe = arquivo.criarClassePublica(raiz.getDTO());

		for (Atributo att : atributos) {
			classe.addCampoPrivado(att.criarVariavel());
		}

		for (Atributo att : atributos) {
			classe.newLine();
			Variavel tipo = att.criarVariavel();
			classe.criarMetodoGet(tipo);
			if (Boolean.TRUE.equals(att.getParseDateBoolean())) {
				classe.newLine();
				Funcao funcao = classe.criarFuncaoPublica("Date", "get" + Util.capitalize(att.getNome()) + "Date");
				funcao.addReturn("DataUtil.parseDate(" + att.getNome() + ")");
			}
			classe.newLine();
			classe.criarMetodoSet(tipo);
		}

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

class PainelFilter extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelFilter(AtributoPagina pagina) {
		super(pagina, false);
	}

	@Override
	String getChaveTitulo() {
		return "label.filter";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		Arquivo arquivo = new Arquivo();
		if (AtributoUtil.contemParseDateValido(atributos)) {
			arquivo.addImport("java.util.Date").newLine();
		}
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
			classe.newLine();
			Variavel tipo = att.criarVariavel();
			classe.criarMetodoGet(tipo);
			if (Boolean.TRUE.equals(att.getParseDateBoolean())) {
				classe.newLine();
				Funcao funcao = classe.criarFuncaoPublica("Date", "get" + Util.capitalize(att.getNome()) + "Date");
				funcao.addReturn("DataUtil.parseDate(" + att.getNome() + ")");
			}
			classe.newLine();
			classe.criarMetodoSet(tipo);
		}

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

class PainelRest extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelRest(AtributoPagina pagina) {
		super(pagina, true);
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

		arquivo.gerar(-1, pool);
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

		String retorno = chkModeloLista.isSelected() ? raiz.getListDTO() : raiz.getDTO();
		FuncaoPublica funcao = classe.criarFuncaoPublica(retorno, AtributoUtil.getPesquisar(mapaRest), beanParam(raiz));
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
		Parametros params = new Parametros("@BeanParam");
		params.addEspaco();
		Variavel var = raiz.getTipoFilter();
		params.addString(var.getTipo() + " " + var.getNome());
		return params;
	}
}

class PainelService extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelService(AtributoPagina pagina) {
		super(pagina, true);
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

		String retorno = chkModeloLista.isSelected() ? raiz.getListDTO() : raiz.getDTO();
		interfacee.criarFuncaoAbstrata(retorno, AtributoUtil.getPesquisar(mapaService),
				new Parametros(raiz.getTipoFilter()));
		interfacee.newLine();

		interfacee.criarFuncaoAbstrata("byte[]", AtributoUtil.getExportar(mapaService),
				new Parametros(new Variavel(raiz.getListDTO(), "dtos")));

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

class PainelBean extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelBean(AtributoPagina pagina) {
		super(pagina, true);
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
		classe.addCampoPrivado(raiz.getTipoDAO());

		classe.addOverride(true);
		String retorno = chkModeloLista.isSelected() ? raiz.getListDTO() : raiz.getDTO();
		Funcao funcao = classe.criarFuncaoPublica(retorno, AtributoUtil.getPesquisar(mapaService),
				new Parametros(raiz.getTipoFilter()));
		funcao.addReturn("dao." + AtributoUtil.getPesquisarFilter(mapaDAO));

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("byte[]", AtributoUtil.getExportar(mapaService),
				new Parametros(new Variavel(raiz.getListDTO(), "dtos")));
		funcao.addReturn("new byte[0]");

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

class PainelDAO extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelDAO(AtributoPagina pagina) {
		super(pagina, true);
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

		String retorno = chkModeloLista.isSelected() ? raiz.getListDTO() : raiz.getDTO();
		interfacee.criarFuncaoAbstrata(retorno, AtributoUtil.getPesquisar(mapaDAO),
				new Parametros(raiz.getTipoFilter()));

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

class PainelDAOImpl extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelDAOImpl(AtributoPagina pagina) {
		super(pagina, true);
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

		classe.addOverride(true);
		String retorno = chkModeloLista.isSelected() ? raiz.getListDTO() : raiz.getDTO();
		Funcao funcao = classe.criarFuncaoPublica(retorno, AtributoUtil.getPesquisar(mapaDAO),
				new Parametros(raiz.getTipoFilter()));
		if (chkModeloLista.isSelected()) {
			funcao.addInstrucao(raiz.getListDTO() + " resp = new ArrayList<>()");
		} else {
			funcao.addInstrucao(raiz.getDTO() + " resp = null");
		}
		funcao.addComentario("entityManager.find...").newLine();
		funcao.addReturn("resp");

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

class PainelTest extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelTest(AtributoPagina pagina) {
		super(pagina, false);
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

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}