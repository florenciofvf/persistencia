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
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.StringPool;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Aba;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
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

public class AtributoPagina extends Panel implements Aba {
	private static final long serialVersionUID = 1L;
	private final PainelAtributo painelAtributo;
	private final PainelFichario painelFichario;
	private transient Raiz raiz;
	private int indice;

	public AtributoPagina(File file) {
		painelAtributo = new PainelAtributo(file);
		painelFichario = new PainelFichario(this);
		montarLayout();
		abrir();
	}

	public Raiz getRaiz() {
		return raiz;
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelAtributo, painelFichario);
		SwingUtilities.invokeLater(() -> split.setDividerLocation(.33));
		split.setOneTouchExpandable(true);
		split.setContinuousLayout(true);
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

	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		if (Util.contemStringEm(painelAtributo.file, string, porParte)) {
			set.add(painelAtributo.file.getAbsolutePath());
		}
	}

	public void setText(String conteudo) {
		painelAtributo.textEditor.setText(conteudo);
	}

	public List<Atributo> getAtributos() {
		return painelAtributo.getAtributos();
	}

	class PainelAtributo extends Panel {
		private final JTable tabela = new JTable(new AtributoModelo());
		public final TextEditor textEditor = new TextEditor();
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
			split.setOneTouchExpandable(true);
			split.setContinuousLayout(true);
			add(BorderLayout.NORTH, toolbar);
			add(BorderLayout.CENTER, split);
		}

		private Panel criarPanelTextArea() {
			Panel panel = new Panel();
			Panel panelArea = new Panel();
			panelArea.add(BorderLayout.CENTER, textEditor);
			scrollPane = new ScrollPane(panelArea);
			scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
			Panel panelScroll = new Panel();
			panelScroll.add(BorderLayout.CENTER, scrollPane);
			panel.add(BorderLayout.CENTER, new ScrollPane(panelScroll));
			textEditor.setListener(e -> toolbar.focusInputPesquisar());
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
			private static final long serialVersionUID = 1L;
			private transient Selecao selecao;

			private Toolbar() {
				super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR);
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
						if (cv.getValor() instanceof Separador) {
							((Separador) cv.getValor()).processar(sb);
							continue;
						}
						Object valor = cv.getValor();
						String toStr = (valor instanceof String) ? Util.citar2(valor.toString())
								: ((Mapa) valor).toString(1);
						sb.append(Mapa.tabular(1) + Util.citar2(cv.getChave()) + ": " + toStr);
					}
					textEditor.setText("{\n" + sb.toString() + "\n}");
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
				String string = Util.getString(textEditor);
				Util.setContentTransfered(string);
				copiarMensagem(string);
				textEditor.requestFocus();
			}

			@Override
			protected void colar(boolean numeros, boolean letras) {
				Util.getContentTransfered(textEditor, numeros, letras);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Util.isEmpty(txtPesquisa.getText())) {
					selecao = Util.getSelecao(textEditor, selecao, txtPesquisa.getText());
					selecao.selecionar(label);
				} else {
					label.limpar();
				}
			}

			private void carregar() {
				try {
					if (!Util.isEmpty(textEditor.getText())) {
						AtributoHandlerImpl handler = new AtributoHandlerImpl();
						AtributoProcessador processador = new AtributoProcessador(handler, textEditor.getText());
						processador.processar();
						raiz = new Raiz(handler.getRaiz());
						painelFichario.selecionarModeloLista(raiz.isPesquisarRetornoLista());
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
				JFileChooser fileChooser = new JFileChooser(
						ArquivoUtil.getValido(AtributoPreferencia.getDirPadraoSelecaoArquivos()));
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
				att.setRotulo(Util.capitalize(nome));
				att.setClasse("String");
				att.setNome(nome);
				return att;
			}

			private Mapa criarMapaHierarquia(Atributo... atributos) {
				Mapa resp = new Mapa();
				AtomicInteger atom = new AtomicInteger(1);
				resp.put(AtributoConstantes.ATRIBUTOS, criarMapaAtributos(atributos));
				separador(resp, atom);
				resp.put(AtributoConstantes.PESQUISAR_RETORNO_LISTA, "true");
				separador(resp, atom);
				resp.put(AtributoConstantes.FILTER_JS, AtributoConstantes.FILTER_JS);
				separador(resp, atom, Constantes.QL);
				resp.put(AtributoConstantes.FILTER_JV, Util.capitalize(AtributoConstantes.FILTER_JV));
				separador(resp, atom, Constantes.QL);
				resp.put(AtributoConstantes.DTO_PESQUISAR, Util.capitalize(AtributoConstantes.DTO_PESQUISAR));
				separador(resp, atom, Constantes.QL);
				resp.put(AtributoConstantes.DTO_DETALHAR, Util.capitalize(AtributoConstantes.DTO_DETALHAR));
				separador(resp, atom, Constantes.QL);
				resp.put(AtributoConstantes.DTO_TODOS, Util.capitalize(AtributoConstantes.DTO_TODOS));
				separador(resp, atom);
				resp.put(AtributoConstantes.CONTROLLER_JS, criarMapa(AtributoConstantes.CONTROLLER_JS,
						new ChaveValor(AtributoConstantes.LIMPAR_FILTRO, AtributoConstantes.LIMPAR_FILTRO)));
				separador(resp, atom);
				resp.put(AtributoConstantes.SERVICE_JS, criarMapa(AtributoConstantes.SERVICE_JS));
				separador(resp, atom);
				resp.put(AtributoConstantes.REST, criarMapa(AtributoConstantes.REST,
						new ChaveValor(AtributoConstantes.END_POINT, AtributoConstantes.END_POINT)));
				separador(resp, atom);
				resp.put(AtributoConstantes.SERVICE, criarMapa(AtributoConstantes.SERVICE));
				separador(resp, atom);
				resp.put(AtributoConstantes.BEAN, Util.capitalize(AtributoConstantes.BEAN));
				separador(resp, atom);
				resp.put(AtributoConstantes.DAO, criarMapaDAO(AtributoConstantes.DAO.toUpperCase()));
				separador(resp, atom);
				resp.put(AtributoConstantes.DAO_IMPL, AtributoConstantes.DAO_IMP2);
				separador(resp, atom);
				resp.put(AtributoConstantes.TEST, criarMapa(AtributoConstantes.TEST));
				return resp;
			}

			void separador(Mapa mapa, AtomicInteger atom, String string) {
				mapa.put(String.valueOf(atom.incrementAndGet()), new Separador(string));
			}

			void separador(Mapa mapa, AtomicInteger atom) {
				separador(mapa, atom, null);
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
				resp.put(AtributoConstantes.BUSCAR_TODOS, AtributoConstantes.BUSCAR_TODOS);
				resp.put(AtributoConstantes.PESQUISAR, AtributoConstantes.PESQUISAR);
				resp.put(AtributoConstantes.DETALHAR, AtributoConstantes.DETALHAR);
				resp.put(AtributoConstantes.EXPORTAR, AtributoConstantes.EXPORTAR);
				for (ChaveValor cv : cvs) {
					resp.put(cv.getChave(), cv.getValor());
				}
				return resp;
			}

			private Mapa criarMapaDAO(String arquivo) {
				Mapa resp = new Mapa();
				resp.put(AtributoConstantes.COMPONENTE, Util.capitalize(arquivo));
				resp.put(AtributoConstantes.BUSCAR_TODOS, AtributoConstantes.BUSCAR_TODOS);
				resp.put(AtributoConstantes.PESQUISAR, AtributoConstantes.PESQUISAR);
				resp.put(AtributoConstantes.DETALHAR, AtributoConstantes.DETALHAR);
				return resp;
			}
		}

		private List<Atributo> getAtributos() {
			return ((AtributoModelo) tabela.getModel()).getLista();
		}

		private String getConteudo() {
			return textEditor.getText();
		}

		private String getNome() {
			return file.getName();
		}

		private void abrir() {
			textEditor.setText(Constantes.VAZIO);
			if (file.exists()) {
				try {
					int value = getValueScrollPane();
					textEditor.setText(ArquivoUtil.getString(file));
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
			try {
				ArquivoUtil.salvar(textEditor, file);
				atomic.set(true);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(AtributoConstantes.PAINEL_ATRIBUTO, ex, this);
			}
		}
	}

	@Override
	public void setIndice(int i) {
		indice = i;
	}

	@Override
	public int getIndice() {
		return indice;
	}

	@Override
	public File getFile() {
		return painelAtributo.file;
	}
}

class PainelFichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	PainelFichario(AtributoPagina pagina) {
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		addAba(new PainelView(pagina));
		addAba(new PainelValidarJS(pagina));
		addAba(new PainelFilterJS(pagina));
		addAba(new PainelControllerJS(pagina));
		addAba(new PainelServiceJS(pagina));
		addAba(new PainelFilterJV(pagina));
		addAba(new PainelRest(pagina));
		addAba(new PainelDTOPesquisa(pagina));
		addAba(new PainelDTODetalhe(pagina));
		addAba(new PainelDTOTodos(pagina));
		addAba(new PainelService(pagina));
		addAba(new PainelBean(pagina));
		addAba(new PainelDAO(pagina));
		addAba(new PainelDAOImpl(pagina));
		addAba(new PainelTest1(pagina));
		addAba(new PainelTest2(pagina));
		addAba(new PainelTest3(pagina));
	}

	private void addAba(AbstratoPanel panel) {
		addTab(AtributoMensagens.getString(panel.getChaveTitulo()), null, panel,
				AtributoMensagens.getString(panel.getChaveTooltip()));
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
	protected final CheckBox chkModeloLista = new CheckBox(AtributoMensagens.getString("label.pesquisar_retorno_lista"),
			false);
	protected final TextEditor textEditor = new TextEditor();
	private static final long serialVersionUID = 1L;
	protected final Toolbar toolbar = new Toolbar();
	private final AtributoPagina pagina;
	protected int contador;

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
		textEditor.setListener(e -> toolbar.focusInputPesquisar());
	}

	void selecionarModeloLista(boolean b) {
		chkModeloLista.setSelected(b);
	}

	private Panel criarPanelTextArea() {
		Panel panel = new Panel();
		Panel panelArea = new Panel();
		panelArea.add(BorderLayout.CENTER, textEditor);
		ScrollPane scrollPane = new ScrollPane(panelArea);
		scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		panel.add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		return panel;
	}

	protected class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		private Toolbar() {
			super.ini(new Nil(), LIMPAR, ATUALIZAR, COPIAR);
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		@Override
		protected void atualizar() {
			List<Atributo> lista = new ArrayList<>();
			for (Atributo att : pagina.getAtributos()) {
				if (!att.isIgnorar()) {
					lista.add(att);
				}
			}
			Raiz raiz = pagina.getRaiz();
			if (raiz != null) {
				gerar(raiz, lista);
			} else {
				Util.mensagem(AbstratoPanel.this, AtributoMensagens.getString("msg.hierarquia_nao_definida"));
			}
		}

		@Override
		protected void limpar() {
			textEditor.setText(Constantes.VAZIO);
			textEditor.requestFocus();
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textEditor);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textEditor.requestFocus();
		}

		protected void excluirAtualizar() {
			removeAction(atualizarAcao);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textEditor, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
	}

	protected void setText(String string) {
		textEditor.setText(string);
	}

	protected void appendText(String string) {
		if (Util.isEmpty(textEditor.getText())) {
			textEditor.setText(string);
		} else {
			textEditor.setText(textEditor.getText() + Constantes.QL2 + string);
		}
	}

	abstract void gerar(Raiz raiz, List<Atributo> atributos);

	abstract String getChaveTooltip();

	abstract String getChaveTitulo();
}

class PainelView extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelView(AtributoPagina pagina) {
		super(pagina, false);
	}

	@Override
	String getChaveTooltip() {
		return "label.view";
	}

	@Override
	String getChaveTitulo() {
		return "label.view";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();

		pool.tab(2).append("<div class='row'>").ql();
		for (Atributo att : atributos) {
			pool.tab(3).append("<div class='col-sm--X'>").ql();
			pool.tab(4).append("{{" + att.getNome() + "}}").ql();
			pool.tab(3).append("</div>").ql();
		}
		pool.tab(2).append("</div>").ql();

		Mapa mapaControllerJS = raiz.getMapaControllerJS();
		if (mapaControllerJS == null) {
			return;
		}

		String nome = AtributoUtil.getPesquisar(mapaControllerJS);
		if (!Util.isEmpty(nome)) {
			pool.ql();
			pool.tab().append("<button id=\"pesquisar\" ng-click=\"vm." + nome
					+ "()\" class=\"btn btn--primary btn--sm m-l-0-5\"><i class=\"i i-search\"></i>Pesquisar</button>");
		}

		nome = AtributoUtil.getExportar(mapaControllerJS);
		if (!Util.isEmpty(nome)) {
			pool.ql();
			pool.tab().append("<button id=\"exportar\" ng-click=\"vm." + nome
					+ "()\" class=\"btn btn--primary btn--sm m-l-0-5\"><i class=\"i i-file-pdf-o\"></i>Exportar PDF</button>");
		}

		nome = mapaControllerJS.getString(AtributoConstantes.LIMPAR_FILTRO);
		if (!Util.isEmpty(nome)) {
			pool.ql();
			pool.tab().append("<button id=\"limpar\" ng-click=\"vm." + nome
					+ "()\" class=\"btn btn--default btn--sm m-l-0-5\">Limpar</button>");
		}

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
	String getChaveTooltip() {
		return "label.controller_js";
	}

	@Override
	String getChaveTitulo() {
		return "label.controller_js";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		if (Util.isEmpty(raiz.getFilterJSPesquisarExportar())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.FILTER_JS);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		String filtro = raiz.getFilterJSPesquisarExportar();
		Mapa mapaControllerJS = raiz.getMapaControllerJS();
		Mapa mapaServiceJS = raiz.getMapaServiceJS();
		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		StringPool pool = new StringPool();
		JSArquivo arquivo = criarArquivo(mapaControllerJS, mapaServiceJS);
		JSFuncao funcao = criarFuncao(arquivo, mapaControllerJS, mapaServiceJS);
		funcao.addInstrucao("var vm = this").newLine();
		funcao.addInstrucao("vm.pesquisados = new NgTableParams()");
		if (!Util.isEmpty(filtro)) {
			funcao.addInstrucao("vm." + filtro + " = {}");
		}

		fnLimparFiltro(funcao, mapaControllerJS, filtro);
		fnBuscarTodos(funcao, mapaControllerJS, mapaServiceJS);
		fnPesquisar(funcao, mapaControllerJS, mapaServiceJS, filtro);
		fnDetalhar(funcao, mapaControllerJS, mapaServiceJS);
		fnExportar(funcao, mapaControllerJS, mapaServiceJS, filtro);

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}

	private void fnLimparFiltro(JSFuncao funcao, Mapa mapaControllerJS, String filtro) {
		String nome = mapaControllerJS.getString(AtributoConstantes.LIMPAR_FILTRO);
		if (Util.isEmpty(nome)) {
			return;
		}
		funcao.newLine();
		JSFuncaoAtributo limpar = funcao.criarJSFuncaoAtributo("vm." + nome);
		limpar.addComentario("$scope.$emit('msgClear');");
		limpar.addInstrucao("vm." + filtro + " = {}");
	}

	private void fnBuscarTodos(JSFuncao funcao, Mapa mapaControllerJS, Mapa mapaServiceJS) {
		String nome = AtributoUtil.getBuscarTodos(mapaControllerJS);
		if (Util.isEmpty(nome)) {
			return;
		}
		funcao.newLine();
		JSFuncaoAtributo buscarTodos = funcao.criarJSFuncaoAtributo("vm." + nome);
		JSInvocaProm invocaProm = buscarTodos.criarJSInvocaProm(AtributoUtil.getComponente(mapaServiceJS) + "."
				+ AtributoUtil.getBuscarTodos(mapaServiceJS) + "().then(function(result) {");
		invocaProm.addInstrucao("var lista = result.data");
	}

	private void fnPesquisar(JSFuncao funcao, Mapa mapaControllerJS, Mapa mapaServiceJS, String filtro) {
		String nome = AtributoUtil.getPesquisar(mapaControllerJS);
		if (Util.isEmpty(nome)) {
			return;
		}
		funcao.newLine();
		JSFuncaoAtributo pesquisar = funcao.criarJSFuncaoAtributo("vm." + nome);
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

	private void fnDetalhar(JSFuncao funcao, Mapa mapaControllerJS, Mapa mapaServiceJS) {
		String nome = AtributoUtil.getDetalhar(mapaControllerJS);
		if (Util.isEmpty(nome)) {
			return;
		}
		funcao.newLine();
		JSFuncaoAtributo detalhar = funcao.criarJSFuncaoAtributo("vm." + nome, new Parametros("item"));
		JSInvocaProm invocaProm = detalhar.criarJSInvocaProm(AtributoUtil.getComponente(mapaServiceJS) + "."
				+ AtributoUtil.getDetalhar(mapaServiceJS) + "({id: item.id}).then(function(result) {");
		invocaProm.addInstrucao("var dto = result.data.plain()");
	}

	private void fnExportar(JSFuncao funcao, Mapa mapaControllerJS, Mapa mapaServiceJS, String filtro) {
		String nome = AtributoUtil.getExportar(mapaControllerJS);
		if (Util.isEmpty(nome)) {
			return;
		}
		funcao.newLine();
		JSFuncaoAtributo exportar = funcao.criarJSFuncaoAtributo("vm." + nome);
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

		fnProcessarFile(funcao);
	}

	private Container fnProcessarFile(JSFuncao funcao) {
		funcao.newLine();
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

class PainelFilterJS extends AbstratoPainelJS {
	private static final long serialVersionUID = 1L;

	PainelFilterJS(AtributoPagina pagina) {
		super(pagina, false);
	}

	@Override
	String getChaveTooltip() {
		return "label.filter_js_tooltip";
	}

	@Override
	String getChaveTitulo() {
		return "label.filter_js";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		if (Util.isEmpty(raiz.getFilterJSPesquisarExportar())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.FILTER_JS);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		String filtro = raiz.getFilterJSPesquisarExportar();
		Mapa mapaControllerJS = raiz.getMapaControllerJS();
		Mapa mapaServiceJS = raiz.getMapaServiceJS();
		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		StringPool pool = new StringPool();
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
	String getChaveTooltip() {
		return "label.validar_js";
	}

	@Override
	String getChaveTitulo() {
		return "label.validar_js";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		if (Util.isEmpty(raiz.getFilterJSPesquisarExportar())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.FILTER_JS);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		String filtro = raiz.getFilterJSPesquisarExportar();
		Mapa mapaControllerJS = raiz.getMapaControllerJS();
		Mapa mapaServiceJS = raiz.getMapaServiceJS();
		if (mapaControllerJS == null || mapaServiceJS == null) {
			return;
		}

		StringPool pool = new StringPool();
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
	private static final String PATH_CUSTOM_GET = "Restangular.all(PATH).customGET('";
	private static final String FILTRO_SUFIX = "', filtro)";
	private transient JSFuncaoPropriedade ultimaAdicionada;
	private static final long serialVersionUID = 1L;

	PainelServiceJS(AtributoPagina pagina) {
		super(pagina, false);
	}

	@Override
	String getChaveTooltip() {
		return "label.service_js";
	}

	@Override
	String getChaveTitulo() {
		return "label.service_js";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		ultimaAdicionada = null;
		Mapa mapaServiceJS = raiz.getMapaServiceJS();
		Mapa mapaRest = raiz.getMapaRest();
		if (mapaServiceJS == null || mapaRest == null) {
			return;
		}

		StringPool pool = new StringPool();
		JSArquivo arquivo = new JSArquivo();
		arquivo.addInstrucao(AtributoUtil.getComponente(mapaServiceJS) + ".$inject = ['Restangular']").newLine();

		Parametros params = new Parametros(new JSVar("Restangular"));
		JSFuncao funcao = arquivo.criarJSFuncao(AtributoUtil.getComponente(mapaServiceJS), params);

		funcao.addInstrucao("var PATH = '" + mapaRest.getString(AtributoConstantes.END_POINT) + "'").newLine();
		JSReturnObj returnObj = funcao.criarJSReturnObj();

		fnBuscarTodos(returnObj, mapaServiceJS, mapaRest);
		fnPesquisar(returnObj, mapaServiceJS, mapaRest);
		fnDetalhar(returnObj, mapaServiceJS, mapaRest);
		fnExportar(returnObj, mapaServiceJS, mapaRest);

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}

	private void fnBuscarTodos(JSReturnObj returnObj, Mapa mapaServiceJS, Mapa mapaRest) {
		String nome = AtributoUtil.getBuscarTodos(mapaServiceJS);
		if (Util.isEmpty(nome)) {
			return;
		}
		JSFuncaoPropriedade funcao = returnObj.criarJSFuncaoPropriedade(false, nome);
		funcao.addReturn(PATH_CUSTOM_GET + AtributoUtil.getBuscarTodos(mapaRest) + "', {})");
		ultimaAdicionada = funcao;
	}

	private void fnPesquisar(JSReturnObj returnObj, Mapa mapaServiceJS, Mapa mapaRest) {
		String nome = AtributoUtil.getPesquisar(mapaServiceJS);
		if (Util.isEmpty(nome)) {
			return;
		}
		if (ultimaAdicionada != null) {
			ultimaAdicionada.setSeparar(true);
		}
		Parametros params = new Parametros(new JSVar(AtributoConstantes.FILTRO));
		JSFuncaoPropriedade funcao = returnObj.criarJSFuncaoPropriedade(false, nome, params);
		funcao.addReturn(PATH_CUSTOM_GET + AtributoUtil.getPesquisar(mapaRest) + FILTRO_SUFIX);
		ultimaAdicionada = funcao;
	}

	private void fnDetalhar(JSReturnObj returnObj, Mapa mapaServiceJS, Mapa mapaRest) {
		String nome = AtributoUtil.getDetalhar(mapaServiceJS);
		if (Util.isEmpty(nome)) {
			return;
		}
		if (ultimaAdicionada != null) {
			ultimaAdicionada.setSeparar(true);
		}
		Parametros params = new Parametros(new JSVar(AtributoConstantes.FILTRO));
		JSFuncaoPropriedade funcao = returnObj.criarJSFuncaoPropriedade(false, nome, params);
		funcao.addReturn(PATH_CUSTOM_GET + AtributoUtil.getDetalhar(mapaRest) + FILTRO_SUFIX);
		ultimaAdicionada = funcao;
	}

	private void fnExportar(JSReturnObj returnObj, Mapa mapaServiceJS, Mapa mapaRest) {
		String nome = AtributoUtil.getExportar(mapaServiceJS);
		if (Util.isEmpty(nome)) {
			return;
		}
		if (ultimaAdicionada != null) {
			ultimaAdicionada.setSeparar(true);
		}
		Parametros params = new Parametros(new JSVar(AtributoConstantes.FILTRO));
		JSFuncaoPropriedade funcao = returnObj.criarJSFuncaoPropriedade(false, nome, params);
		funcao.addReturn("Restangular.all(PATH).withHttpConfig({responseType: \"arraybuffer\"}).customGET('"
				+ AtributoUtil.getExportar(mapaRest) + FILTRO_SUFIX);
		ultimaAdicionada = funcao;
	}
}

abstract class AbstratoDTO extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	AbstratoDTO(AtributoPagina pagina, boolean comCheckModelo) {
		super(pagina, comCheckModelo);
	}

	protected void camposEGetsESets(List<Atributo> atributos, ClassePublica classe) {
		for (Atributo att : atributos) {
			classe.addCampoPrivado(att.criarVariavel());
		}
		getsESets(atributos, classe);
	}

	static void getsESets(List<Atributo> atributos, ClassePublica classe) {
		for (Atributo att : atributos) {
			classe.newLine();
			Variavel tipo = att.criarVariavel();
			classe.criarMetodoGet(tipo);
			if (Boolean.TRUE.equals(att.getParseDateBoolean())) {
				classe.newLine();
				Funcao funcao = classe.criarFuncaoPublica("Date", "get" + Util.capitalize(att.getNome()) + "Date");
				funcao.addReturn(AtributoConstantes.DATA_UTIL_PARSE_DATE + att.getNome() + ")");
			}
			if (Boolean.TRUE.equals(att.getParseLongBoolean())) {
				classe.newLine();
				Funcao funcao = classe.criarFuncaoPublica("Long", "get" + Util.capitalize(att.getNome()) + "Long");
				funcao.addReturn(AtributoConstantes.UTIL_PARSE_LONG + att.getNome() + ")");
			}
			classe.newLine();
			classe.criarMetodoSet(tipo);
		}
	}
}

class PainelDTOPesquisa extends AbstratoDTO {
	private static final long serialVersionUID = 1L;

	PainelDTOPesquisa(AtributoPagina pagina) {
		super(pagina, false);
	}

	@Override
	String getChaveTooltip() {
		return "label.dto_pesquisa_tooltip";
	}

	@Override
	String getChaveTitulo() {
		return "label.dto_pesquisa";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		if (Util.isEmpty(raiz.getDTOPesquisa())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.DTO_PESQUISAR);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		arquivo.addAnotacao(AtributoConstantes.IGNORE_PROPERTIES);
		if (AtributoUtil.contemParseDateValido(atributos)) {
			arquivo.addImport(AtributoConstantes.JAVA_UTIL_DATE).newLine();
		}

		ClassePublica classe = arquivo.criarClassePublica(raiz.getDTOPesquisa());
		camposEGetsESets(atributos, classe);
		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

class PainelDTOTodos extends AbstratoDTO {
	private static final long serialVersionUID = 1L;

	PainelDTOTodos(AtributoPagina pagina) {
		super(pagina, false);
	}

	@Override
	String getChaveTooltip() {
		return "label.dto_todos_tooltip";
	}

	@Override
	String getChaveTitulo() {
		return "label.dto_todos";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		if (Util.isEmpty(raiz.getDTOTodos())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.DTO_TODOS);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		arquivo.addAnotacao(AtributoConstantes.IGNORE_PROPERTIES);
		if (AtributoUtil.contemParseDateValido(atributos)) {
			arquivo.addImport(AtributoConstantes.JAVA_UTIL_DATE).newLine();
		}

		ClassePublica classe = arquivo.criarClassePublica(raiz.getDTOTodos());
		camposEGetsESets(atributos, classe);
		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

class PainelDTODetalhe extends AbstratoDTO {
	private static final long serialVersionUID = 1L;

	PainelDTODetalhe(AtributoPagina pagina) {
		super(pagina, false);
	}

	@Override
	String getChaveTooltip() {
		return "label.dto_detalhe_tooltip";
	}

	@Override
	String getChaveTitulo() {
		return "label.dto_detalhe";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		if (Util.isEmpty(raiz.getDTODetalhe())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.DTO_DETALHAR);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		arquivo.addAnotacao(AtributoConstantes.IGNORE_PROPERTIES);
		if (AtributoUtil.contemParseDateValido(atributos)) {
			arquivo.addImport(AtributoConstantes.JAVA_UTIL_DATE).newLine();
		}

		ClassePublica classe = arquivo.criarClassePublica(raiz.getDTODetalhe());
		camposEGetsESets(atributos, classe);
		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

class PainelFilterJV extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelFilterJV(AtributoPagina pagina) {
		super(pagina, false);
	}

	@Override
	String getChaveTooltip() {
		return "label.filter_jv_tooltip";
	}

	@Override
	String getChaveTitulo() {
		return "label.filter_jv";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		if (Util.isEmpty(raiz.getFilterJVPesquisarExportar())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.FILTER_JV);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		if (AtributoUtil.contemParseDateValido(atributos)) {
			arquivo.addImport(AtributoConstantes.JAVA_UTIL_DATE).newLine();
		}
		if (!atributos.isEmpty()) {
			arquivo.addImport("javax.ws.rs.QueryParam").newLine();
		}

		ClassePublica classe = arquivo.criarClassePublica(raiz.getFilterJVPesquisarExportar());

		int i = 0;
		for (Atributo att : atributos) {
			if (i++ > 0) {
				classe.newLine();
			}
			classe.addAnotacao("QueryParam(" + Util.citar2(att.getNome()) + ")");
			classe.addCampoPrivado(att.criarVariavel());
		}

		AbstratoDTO.getsESets(atributos, classe);
		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

class PainelRest extends AbstratoPanel {
	private static final String CONSUMES = "Consumes(";
	private static final String PRODUCES = "Produces(";
	private static final long serialVersionUID = 1L;
	private String servicePdfDot;
	private String serviceDot;

	PainelRest(AtributoPagina pagina) {
		super(pagina, true);
	}

	@Override
	String getChaveTooltip() {
		return "label.rest";
	}

	@Override
	String getChaveTitulo() {
		return "label.rest";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		if (Util.isEmpty(raiz.getFilterJVPesquisarExportar())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.FILTER_JV);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		StringPool pool = new StringPool();
		Mapa mapaService = raiz.getMapaService();
		Mapa mapaRest = raiz.getMapaRest();
		if (mapaService == null || mapaRest == null) {
			return;
		}

		String service = AtributoUtil.getComponente(mapaService);
		if (Util.isEmpty(service)) {
			service = "service";
		}
		serviceDot = Util.decapitalize(service + ".");
		String servicePdf = service + "PDF";
		servicePdfDot = Util.decapitalize(servicePdf + ".");

		Arquivo arquivo = new Arquivo();
		arquivo.addImport(AtributoConstantes.IMPORT_LIST).newLine();
		arquivo.addImport("javax.inject.Inject").newLine();
		arquivo.addImport("javax.ws.rs.Consumes");
		arquivo.addImport("javax.ws.rs.Produces");
		arquivo.addImport("javax.ws.rs.BeanParam");
		arquivo.addImport("javax.ws.rs.GET");
		arquivo.addImport("javax.ws.rs.Path");
		arquivo.addImport("javax.ws.rs.Produces");
		arquivo.addImport("javax.ws.rs.QueryParam");
		arquivo.addImport("javax.ws.rs.core.MediaType");
		arquivo.addImport("javax.ws.rs.core.Response");
		arquivo.addImport("javax.ws.rs.core.Response.ResponseBuilder").newLine();
		arquivo.addComentario("br.com.empresa.framework.seguranca.RestSeguranca;").newLine();
		arquivo.addAnotacaoPath(mapaRest.getString(AtributoConstantes.END_POINT));

		ClassePublica classe = arquivo
				.criarClassePublica(AtributoUtil.getComponente(mapaRest) + " extends ApplicationRest");

		injetar(classe, new Variavel(service, Util.decapitalize(service))).newLine();
		injetar(classe, new Variavel(servicePdf, Util.decapitalize(servicePdf)));
		criarBuscarTodos(raiz, mapaRest, mapaService, classe);
		criarPesquisar(raiz, mapaRest, mapaService, classe);
		criarDetalhar(raiz, mapaRest, mapaService, classe);
		criarExportar(raiz, mapaRest, mapaService, classe);

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}

	private ClassePublica injetar(ClassePublica classe, Variavel tipo) {
		classe.addAnotacao("Inject");
		classe.addCampoPrivado(tipo);
		return classe;
	}

	private void criarBuscarTodos(Raiz raiz, Mapa mapaRest, Mapa mapaService, ClassePublica classe) {
		String nome = AtributoUtil.getBuscarTodos(mapaRest);
		if (Util.isEmpty(nome)) {
			return;
		}
		classe.newLine();
		classe.addAnotacao("GET");
		classe.addAnotacaoPath(nome);
		classe.addAnotacao(CONSUMES + AtributoConstantes.APPLICATION_JSON + ")");
		classe.addAnotacao(PRODUCES + AtributoConstantes.APPLICATION_JSON + ")");
		FuncaoPublica funcao = classe.criarFuncaoPublica(raiz.getListDTOTodos(), nome);
		funcao.addReturn(serviceDot + AtributoUtil.getBuscarTodos(mapaService) + "()");
	}

	private void criarPesquisar(Raiz raiz, Mapa mapaRest, Mapa mapaService, ClassePublica classe) {
		String nome = AtributoUtil.getPesquisar(mapaRest);
		if (Util.isEmpty(nome)) {
			return;
		}
		classe.newLine();
		classe.addAnotacao("GET");
		classe.addAnotacaoPath(nome);
		classe.addAnotacao(CONSUMES + AtributoConstantes.APPLICATION_JSON + ")");
		classe.addAnotacao(PRODUCES + AtributoConstantes.APPLICATION_JSON + ")");

		String retorno = chkModeloLista.isSelected() ? raiz.getListDTOPesquisa() : raiz.getDTOPesquisa();
		FuncaoPublica funcao = classe.criarFuncaoPublica(retorno, nome, beanParam(raiz));
		funcao.addReturn(serviceDot + AtributoUtil.getPesquisarFilter(mapaService));
	}

	private void criarDetalhar(Raiz raiz, Mapa mapaRest, Mapa mapaService, ClassePublica classe) {
		String nome = AtributoUtil.getDetalhar(mapaRest);
		if (Util.isEmpty(nome)) {
			return;
		}

		classe.newLine();
		classe.addAnotacao("GET");
		classe.addAnotacaoPath(nome);
		classe.addAnotacao(CONSUMES + AtributoConstantes.APPLICATION_JSON + ")");
		classe.addAnotacao(PRODUCES + AtributoConstantes.APPLICATION_JSON + ")");

		FuncaoPublica funcao = classe.criarFuncaoPublica(raiz.getDTODetalhe(), nome,
				new Parametros("@QueryParam(\"id\") Long id"));
		funcao.addReturn(serviceDot + AtributoUtil.getDetalhar(mapaService) + "(id)");
	}

	private void criarExportar(Raiz raiz, Mapa mapaRest, Mapa mapaService, ClassePublica classe) {
		String nome = AtributoUtil.getExportar(mapaRest);
		if (Util.isEmpty(nome)) {
			return;
		}

		classe.newLine();
		classe.addAnotacao("GET");
		classe.addAnotacaoPath(nome);
		classe.addAnotacao(CONSUMES + AtributoConstantes.APPLICATION_JSON + ")");
		classe.addAnotacao(PRODUCES + "{MediaType.APPLICATION_OCTET_STREAM}" + ")");

		FuncaoPublica funcao = classe.criarFuncaoPublica("Response", nome, beanParam(raiz));
		if (chkModeloLista.isSelected()) {
			funcao.addInstrucao(
					raiz.getListDTOPesquisa() + " dtos = " + serviceDot + AtributoUtil.getPesquisarFilter(mapaService));
			funcao.addInstrucao(AtributoConstantes.BYTE_ARRAY + " bytes = " + servicePdfDot
					+ AtributoUtil.getExportar(mapaService) + "(dtos)").newLine();
		} else {
			funcao.addInstrucao(
					raiz.getDTOPesquisa() + " dto = " + serviceDot + AtributoUtil.getPesquisarFilter(mapaService));
			funcao.addInstrucao(AtributoConstantes.BYTE_ARRAY + " bytes = " + servicePdfDot
					+ AtributoUtil.getExportar(mapaService) + "(dto)").newLine();
		}
		funcao.addInstrucao("ResponseBuilder response = Response.ok(bytes)");
		funcao.addInstrucao("response.header(\"Content-Disposition\", \"attachment;filename=arquivo.pdf\")");
		funcao.addInstrucao("response.header(\"Content-type\", MediaType.APPLICATION_OCTET_STREAM)");
		funcao.addReturn("response.build()");
	}

	private Parametros beanParam(Raiz raiz) {
		Parametros params = new Parametros("@BeanParam");
		params.addEspaco();
		Variavel varParam = raiz.getTipoFilterJVPesquisarExportar();
		params.addString(varParam.getTipo() + " " + varParam.getNome());
		return params;
	}
}

class PainelService extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelService(AtributoPagina pagina) {
		super(pagina, true);
	}

	@Override
	String getChaveTooltip() {
		return "label.service_tooltip";
	}

	@Override
	String getChaveTitulo() {
		return "label.service";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		contador = 0;
		if (Util.isEmpty(raiz.getFilterJVPesquisarExportar())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.FILTER_JV);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		arquivo.addImport(AtributoConstantes.IMPORT_LIST).newLine();
		arquivo.addImport("javax.ejb.Local").newLine();
		arquivo.addAnotacao("Local");

		Mapa mapaService = raiz.getMapaService();
		if (mapaService == null) {
			return;
		}

		InterfacePublica interfacee = arquivo.criarInterfacePublica(AtributoUtil.getComponente(mapaService));

		String nome = AtributoUtil.getBuscarTodos(mapaService);
		if (!Util.isEmpty(nome)) {
			interfacee.criarFuncaoAbstrata(raiz.getListDTOTodos(), nome);
			contador++;
		}

		nome = AtributoUtil.getPesquisar(mapaService);
		if (!Util.isEmpty(nome)) {
			String retorno = chkModeloLista.isSelected() ? raiz.getListDTOPesquisa() : raiz.getDTOPesquisa();
			checkNewLine(interfacee);
			interfacee.criarFuncaoAbstrata(retorno, nome, new Parametros(raiz.getTipoFilterJVPesquisarExportar()));
			contador++;
		}

		nome = AtributoUtil.getDetalhar(mapaService);
		if (!Util.isEmpty(nome)) {
			checkNewLine(interfacee);
			interfacee.criarFuncaoAbstrata(raiz.getDTODetalhe(), nome, new Parametros(AtributoConstantes.LONG_ID));
			contador++;
		}

		nome = AtributoUtil.getExportar(mapaService);
		if (!Util.isEmpty(nome)) {
			checkNewLine(interfacee);
			if (chkModeloLista.isSelected()) {
				interfacee.criarFuncaoAbstrata(AtributoConstantes.BYTE_ARRAY, nome,
						new Parametros(new Variavel(raiz.getListDTOPesquisa(), "dtos")));
			} else {
				interfacee.criarFuncaoAbstrata(AtributoConstantes.BYTE_ARRAY, nome,
						new Parametros(new Variavel(raiz.getDTOPesquisa(), "dto")));
			}
			contador++;
		}

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}

	private void checkNewLine(InterfacePublica interfacee) {
		if (contador > 0) {
			interfacee.newLine();
		}
	}
}

class PainelBean extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	PainelBean(AtributoPagina pagina) {
		super(pagina, true);
	}

	@Override
	String getChaveTooltip() {
		return "label.bean";
	}

	@Override
	String getChaveTitulo() {
		return "label.bean";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		if (Util.isEmpty(raiz.getFilterJVPesquisarExportar())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.FILTER_JV);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		arquivo.addImport(AtributoConstantes.IMPORT_LIST).newLine();
		arquivo.addImport("javax.ejb.LocalBean");
		arquivo.addImport("javax.ejb.Stateless");
		arquivo.addImport("javax.ejb.TransactionManagement");
		arquivo.addImport("javax.ejb.TransactionManagementType").newLine();
		arquivo.addAnotacao("Stateless");
		arquivo.addAnotacao("LocalBean");
		arquivo.addAnotacao("TransactionManagement(TransactionManagementType.CONTAINER)");

		String bean = raiz.getBean();
		Mapa mapaDAO = raiz.getMapaDAO();
		Mapa mapaService = raiz.getMapaService();
		if (mapaDAO == null || mapaService == null) {
			return;
		}

		ClassePublica classe = arquivo
				.criarClassePublica(bean + " implements " + AtributoUtil.getComponente(mapaService));

		Variavel varDAO = raiz.getTipoDAO();
		String daoDot = varDAO.getNome() + ".";
		classe.addAnotacao("Inject");
		classe.addCampoPrivado(varDAO);

		Funcao funcao = null;
		String nome = AtributoUtil.getBuscarTodos(mapaService);
		if (!Util.isEmpty(nome)) {
			classe.addOverride(true);
			funcao = classe.criarFuncaoPublica(raiz.getListDTOTodos(), nome);
			funcao.addReturn(daoDot + AtributoUtil.getBuscarTodos(mapaDAO) + "()");
		}

		nome = AtributoUtil.getPesquisar(mapaService);
		if (!Util.isEmpty(nome)) {
			String retorno = chkModeloLista.isSelected() ? raiz.getListDTOPesquisa() : raiz.getDTOPesquisa();
			classe.addOverride(true);
			funcao = classe.criarFuncaoPublica(retorno, nome, new Parametros(raiz.getTipoFilterJVPesquisarExportar()));
			funcao.addReturn(daoDot + AtributoUtil.getPesquisarFilter(mapaDAO));
		}

		nome = AtributoUtil.getDetalhar(mapaService);
		if (!Util.isEmpty(nome)) {
			classe.addOverride(true);
			funcao = classe.criarFuncaoPublica(raiz.getDTODetalhe(), nome, new Parametros(AtributoConstantes.LONG_ID));
			funcao.addReturn(daoDot + AtributoUtil.getDetalhar(mapaDAO) + "(id)");
		}

		nome = AtributoUtil.getExportar(mapaService);
		if (!Util.isEmpty(nome)) {
			classe.addOverride(true);
			if (chkModeloLista.isSelected()) {
				funcao = classe.criarFuncaoPublica(AtributoConstantes.BYTE_ARRAY, nome,
						new Parametros(new Variavel(raiz.getListDTOPesquisa(), "dtos")));
			} else {
				funcao = classe.criarFuncaoPublica(AtributoConstantes.BYTE_ARRAY, nome,
						new Parametros(new Variavel(raiz.getDTOPesquisa(), "dto")));
			}
			funcao.addReturn("new byte[0]");
		}

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
	String getChaveTooltip() {
		return "label.dao";
	}

	@Override
	String getChaveTitulo() {
		return "label.dao";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		contador = 0;
		if (Util.isEmpty(raiz.getFilterJVPesquisarExportar())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.FILTER_JV);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		arquivo.addImport(AtributoConstantes.IMPORT_LIST).newLine();

		Mapa mapaDAO = raiz.getMapaDAO();
		if (mapaDAO == null) {
			return;
		}

		InterfacePublica interfacee = arquivo.criarInterfacePublica(AtributoUtil.getComponente(mapaDAO));

		String nome = AtributoUtil.getBuscarTodos(mapaDAO);
		if (!Util.isEmpty(nome)) {
			interfacee.criarFuncaoAbstrata(raiz.getListDTOTodos(), nome);
			contador++;
		}

		nome = AtributoUtil.getPesquisar(mapaDAO);
		if (!Util.isEmpty(nome)) {
			String retorno = chkModeloLista.isSelected() ? raiz.getListDTOPesquisa() : raiz.getDTOPesquisa();
			checkNewLine(interfacee);
			interfacee.criarFuncaoAbstrata(retorno, nome, new Parametros(raiz.getTipoFilterJVPesquisarExportar()));
			contador++;
		}

		nome = AtributoUtil.getDetalhar(mapaDAO);
		if (!Util.isEmpty(nome)) {
			checkNewLine(interfacee);
			interfacee.criarFuncaoAbstrata(raiz.getDTODetalhe(), nome, new Parametros(AtributoConstantes.LONG_ID));
			contador++;
		}

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}

	private void checkNewLine(InterfacePublica interfacee) {
		if (contador > 0) {
			interfacee.newLine();
		}
	}
}

class PainelDAOImpl extends AbstratoPanel {
	private static final String ENTITY_MANAGER_FIND = "entityManager.find...";
	private static final long serialVersionUID = 1L;

	PainelDAOImpl(AtributoPagina pagina) {
		super(pagina, true);
	}

	@Override
	String getChaveTooltip() {
		return "label.dao_impl";
	}

	@Override
	String getChaveTitulo() {
		return "label.dao_impl";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		if (Util.isEmpty(raiz.getFilterJVPesquisarExportar())) {
			String msg = AtributoMensagens.getString(AtributoConstantes.MSG_PROP_NAO_DEFINIDA,
					AtributoConstantes.FILTER_JV);
			if (!Util.confirmar(this, msg, false)) {
				return;
			}
		}

		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		arquivo.addImport("java.util.ArrayList");
		arquivo.addImport(AtributoConstantes.IMPORT_LIST).newLine();
		arquivo.addImport("javax.persistence.EntityManager");
		arquivo.addImport("javax.persistence.PersistenceContext").newLine();

		String daoImpl = raiz.getDAOImpl();
		Mapa mapaDAO = raiz.getMapaDAO();
		if (mapaDAO == null) {
			return;
		}

		ClassePublica classe = arquivo
				.criarClassePublica(daoImpl + " implements " + AtributoUtil.getComponente(mapaDAO));

		classe.addAnotacao("PersistenceContext(" + "unitName = " + Util.citar2("nomeUnit") + ")");
		classe.addCampoPrivado(new Variavel("EntityManager", "entityManager"));

		Funcao funcao = null;
		String nome = AtributoUtil.getBuscarTodos(mapaDAO);
		if (!Util.isEmpty(nome)) {
			classe.addOverride(true);
			funcao = classe.criarFuncaoPublica(raiz.getListDTOTodos(), nome);
			funcao.addComentario("String consulta = \"SELECT obj FROM " + raiz.getDTOTodos() + " obj\";").newLine();
			funcao.addInstrucao(raiz.getListDTOTodos() + " resp = new ArrayList<>()");
			funcao.addComentario(ENTITY_MANAGER_FIND).newLine();
			funcao.addReturn("resp");
		}

		nome = AtributoUtil.getPesquisar(mapaDAO);
		if (!Util.isEmpty(nome)) {
			String retorno = chkModeloLista.isSelected() ? raiz.getListDTOPesquisa() : raiz.getDTOPesquisa();
			classe.addOverride(true);
			funcao = classe.criarFuncaoPublica(retorno, nome, new Parametros(raiz.getTipoFilterJVPesquisarExportar()));
			if (chkModeloLista.isSelected()) {
				funcao.addInstrucao(raiz.getListDTOPesquisa() + " resp = new ArrayList<>()");
			} else {
				funcao.addInstrucao(raiz.getDTOPesquisa() + " resp = null");
			}
			funcao.addComentario(ENTITY_MANAGER_FIND).newLine();
			funcao.addReturn("resp");
		}

		nome = AtributoUtil.getDetalhar(mapaDAO);
		if (!Util.isEmpty(nome)) {
			classe.addOverride(true);
			funcao = classe.criarFuncaoPublica(raiz.getDTODetalhe(), nome, new Parametros(AtributoConstantes.LONG_ID));
			funcao.addComentario(
					"String consulta = \"SELECT obj FROM " + raiz.getDTODetalhe() + " obj WHERE obj.id = :id\";")
					.newLine();
			funcao.addInstrucao(raiz.getDTODetalhe() + " resp = null");
			funcao.addComentario(ENTITY_MANAGER_FIND).newLine();
			funcao.addReturn("resp");
		}

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

abstract class AbstratoTest extends AbstratoPanel {
	private static final long serialVersionUID = 1L;

	AbstratoTest(AtributoPagina pagina, boolean semAtualizar) {
		super(pagina, false);
		if (semAtualizar) {
			toolbar.excluirAtualizar();
		}
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		//
	}

	protected void adicionarImports(Arquivo arquivo, Class<?> classe, String nomeClasse, boolean comMockito) {
		arquivo.addImport("org.junit.Before");
		arquivo.addImport("org.junit.Test").newLine();
		if (comMockito) {
			arquivo.addImport("static org.mockito.ArgumentMatchers.any");
			arquivo.addImport("static org.mockito.Mockito.when").newLine();
			arquivo.addImport("org.junit.runner.RunWith");
			arquivo.addImport("org.mockito.InjectMocks");
			arquivo.addImport("org.mockito.Mock");
			arquivo.addImport("org.mockito.junit.MockitoJUnitRunner").newLine();
		}
		arquivo.addImport("static org.junit.Assert.assertEquals").newLine();
		if (classe != null) {
			arquivo.addImport(classe.getName()).newLine();
		}
		if (nomeClasse != null) {
			arquivo.addImport(nomeClasse).newLine();
		}
		if (comMockito) {
			arquivo.addAnotacao("RunWith(MockitoJUnitRunner.class)");
		}
	}

	protected void criarPreTest(ClassePublica classe) {
		classe.addAnotacao("Before");
		Funcao funcao = classe.criarFuncaoPublica("void", "setUp");
		funcao.addComentario("when(dao.metodo(any())).thenReturn(newObjeto());");
	}
}

class PainelTest1 extends AbstratoTest {
	private final CheckBox chkMockito = new CheckBox(AtributoMensagens.getString("label.mockito"), false);
	private static final long serialVersionUID = 1L;

	PainelTest1(AtributoPagina pagina) {
		super(pagina, false);
		toolbar.add(chkMockito);
	}

	@Override
	String getChaveTooltip() {
		return "label.test_tooltip";
	}

	@Override
	String getChaveTitulo() {
		return "label.test";
	}

	@Override
	void gerar(Raiz raiz, List<Atributo> atributos) {
		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		adicionarImports(arquivo, null, null, chkMockito.isSelected());

		Mapa mapaService = raiz.getMapaService();
		Mapa mapaTest = raiz.getMapaTest();
		if (mapaService == null || mapaTest == null) {
			return;
		}

		ClassePublica classe = arquivo.criarClassePublica(AtributoUtil.getComponente(mapaTest));

		if (chkMockito.isSelected()) {
			classe.addAnotacao("InjectMocks");
		}
		classe.addCampoPrivado(new Variavel(AtributoUtil.getComponente(mapaService), "service"));

		classe.newLine();
		criarPreTest(classe);

		Funcao funcao = null;
		String nome = AtributoUtil.getBuscarTodos(mapaTest);
		if (!Util.isEmpty(nome)) {
			classe.newLine();
			classe.addAnotacao("Test");
			funcao = classe.criarFuncaoPublica("void", nome);
			funcao.addComentario("...");
		}

		nome = AtributoUtil.getPesquisar(mapaTest);
		if (!Util.isEmpty(nome)) {
			classe.newLine();
			classe.addAnotacao("Test");
			funcao = classe.criarFuncaoPublica("void", nome);
			funcao.addComentario("...");
		}

		nome = AtributoUtil.getDetalhar(mapaTest);
		if (!Util.isEmpty(nome)) {
			classe.newLine();
			classe.addAnotacao("Test");
			funcao = classe.criarFuncaoPublica("void", nome);
			funcao.addComentario("...");
		}

		nome = AtributoUtil.getExportar(mapaTest);
		if (!Util.isEmpty(nome)) {
			classe.newLine();
			classe.addAnotacao("Test");
			funcao = classe.criarFuncaoPublica("void", nome);
			funcao.addComentario("...");
		}

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}
}

class PainelTest2 extends AbstratoTest {
	private TextField txtArquivo = new TextField(25);
	private ButtonOpcoes opcoes = new ButtonOpcoes();
	private static final long serialVersionUID = 1L;
	private List<String> linhasArquivo;

	PainelTest2(AtributoPagina pagina) {
		super(pagina, true);
		toolbar.add(txtArquivo);
		Action gerarTestAcao = Action.acaoMenu(AtributoMensagens.getString("label.gerar_teste"), Icones.SINCRONIZAR);
		Action arquivoAction = Action.acaoMenu(AtributoMensagens.getString("label.arquivo_java"), Icones.ABRIR);
		txtArquivo.setToolTipText(AtributoMensagens.getString("label.test2_tooltip_txt_arquivo"));
		gerarTestAcao.setActionListener(e -> gerarTeste());
		arquivoAction.setActionListener(e -> lerArquivo());
		toolbar.addButton(arquivoAction);
		toolbar.addSeparator();
		toolbar.add(opcoes);
		toolbar.addSeparator();
		toolbar.addButton(gerarTestAcao);
	}

	private class ButtonOpcoes extends ButtonPopup {
		private final JCheckBoxMenuItem chkConstrutor = new JCheckBoxMenuItem(
				AtributoMensagens.getString("label.construtor"));
		private final JCheckBoxMenuItem chkMockito = new JCheckBoxMenuItem(
				AtributoMensagens.getString("label.mockito"));
		private final JCheckBoxMenuItem chkOutros = new JCheckBoxMenuItem(AtributoMensagens.getString("label.outros"));
		private final JCheckBoxMenuItem chkSetUp = new JCheckBoxMenuItem(AtributoMensagens.getString("label.setup"));
		private final JCheckBoxMenuItem chkGet = new JCheckBoxMenuItem(AtributoMensagens.getString("label.get"));
		private final JCheckBoxMenuItem chkSet = new JCheckBoxMenuItem(AtributoMensagens.getString("label.set"));
		private final JCheckBoxMenuItem chkIs = new JCheckBoxMenuItem(AtributoMensagens.getString("label.is"));
		private static final long serialVersionUID = 1L;

		private ButtonOpcoes() {
			super("label.opcoes", Icones.TAG2);
			addItem(chkMockito);
			addSeparator();
			addItem(chkConstrutor);
			addSeparator();
			addItem(chkSetUp);
			addItem(chkGet);
			addItem(chkSet);
			addItem(chkIs);
			addSeparator();
			addItem(chkOutros);
		}
	}

	private void lerArquivo() {
		JFileChooser fileChooser = new JFileChooser(
				ArquivoUtil.getValido(AtributoPreferencia.getDirPadraoSelecaoArquivos()));
		int i = fileChooser.showOpenDialog(PainelTest2.this);
		if (i == JFileChooser.APPROVE_OPTION) {
			File sel = fileChooser.getSelectedFile();
			lerArquivo(sel);
		}
	}

	private void lerArquivo(File file) {
		String string = "package";
		String strPackage = ArquivoUtil.primeiroIniciadoCom(string, file);
		if (Util.isEmpty(strPackage)) {
			return;
		}
		strPackage = strPackage.substring(string.length()).trim();
		if (strPackage.endsWith(";")) {
			strPackage = strPackage.substring(0, strPackage.length() - 1);
		}
		String nome = file.getName();
		int pos = nome.lastIndexOf(".");
		if (pos != -1) {
			nome = nome.substring(0, pos);
		}
		linhasArquivo = ArquivoUtil.lerArquivo(file);
		txtArquivo.setText(strPackage + "." + nome);
	}

	private String getNomeClasse(boolean absoluto) {
		String nome = txtArquivo.getText().trim();
		if (absoluto) {
			return nome;
		}
		int pos = nome.lastIndexOf(".");
		return pos == -1 ? nome : nome.substring(pos + 1);
	}

	private void gerarTeste() {
		if (Util.isEmpty(txtArquivo.getText())) {
			Util.mensagem(PainelTest2.this, AtributoMensagens.getString("msg.classe_teste_nao_definida"));
			return;
		}
		String objeto = getNomeClasse(false);
		MetodoHandle metodoHandle = new MetodoHandle(linhasArquivo);
		metodoHandle.processar(opcoes.chkGet.isSelected(), opcoes.chkSet.isSelected(), opcoes.chkIs.isSelected(),
				opcoes.chkConstrutor.isSelected(), objeto, opcoes.chkOutros.isSelected());

		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();
		adicionarImports(arquivo, null, getNomeClasse(true), opcoes.chkMockito.isSelected());

		ClassePublica classeTest = arquivo.criarClassePublica(objeto + "Test");

		if (opcoes.chkMockito.isSelected()) {
			classeTest.addAnotacao("InjectMocks");
			classeTest.addCampoPrivado(new Variavel("Bean", "bean")).newLine();

			classeTest.addAnotacao("Mock");
			classeTest.addCampoPrivado(new Variavel("DAO", "dao")).newLine();
		}

		if (opcoes.chkSetUp.isSelected()) {
			criarPreTest(classeTest);
		}

		testes(classeTest, metodoHandle.getMetodos());

		arquivo.gerar(-1, pool);
		setText(pool.toString());
	}

	@Override
	String getChaveTooltip() {
		return "label.test2_tooltip";
	}

	@Override
	String getChaveTitulo() {
		return "label.test2";
	}

	private void testes(ClassePublica classe, List<Metodo> imetodos) {
		List<Teste> testes = new ArrayList<>();
		for (Metodo item : imetodos) {
			Teste teste = new Teste(item);
			testes.add(teste);
		}
		for (Teste item : testes) {
			item.gerar(classe);
		}
	}

	private class Teste {
		final Metodo metodo;

		Teste(Metodo metodo) {
			this.metodo = metodo;
		}

		void gerar(ClassePublica classe) {
			classe.newLine();
			classe.addAnotacao("Test");
			Funcao funcao = classe.criarFuncaoPublica("void", metodo.getNome() + "Test");
			funcao.addComentario("bean." + metodo.getNome() + "();");
		}
	}
}

class PainelTest3 extends AbstratoTest {
	private final CheckBox chkSetText = new CheckBox(AtributoMensagens.getString("label.setText"), false);
	private TextField txtNomeClasse = new TextField(25);
	private static final long serialVersionUID = 1L;

	PainelTest3(AtributoPagina pagina) {
		super(pagina, true);
		toolbar.add(txtNomeClasse);
		Action newObjetoAcao = Action.acaoMenu(AtributoMensagens.getString("label.novo_objeto"), Icones.CRIAR);
		txtNomeClasse.setToolTipText(AtributoMensagens.getString("label.test3_tooltip_txt_nome_classe"));
		newObjetoAcao.setActionListener(e -> novoObjeto());
		toolbar.add(chkSetText);
		toolbar.addButton(newObjetoAcao);
	}

	private void novoObjeto() {
		if (Util.isEmpty(txtNomeClasse.getText())) {
			Util.mensagem(PainelTest3.this, AtributoMensagens.getString("label.nome_classe"));
			return;
		}
		gerarFragmento(txtNomeClasse.getText().trim());
	}

	private void gerarFragmento(String nomeClasse) {
		nomeClasse = Util.semEspacos(nomeClasse);
		StringPool pool = new StringPool();
		Arquivo arquivo = new Arquivo();

		ClassePublica classe = arquivo.criarClassePublica("Temp");

		Funcao funcao = classe.criarFuncaoPublicaEstatica("List<" + nomeClasse + ">", "newList" + nomeClasse);
		funcao.addInstrucao("List<" + nomeClasse + "> resp = new ArrayList<>()");
		funcao.addInstrucao("resp.add(new" + nomeClasse + "())");
		funcao.addReturn("resp");

		classe.newLine();

		funcao = classe.criarFuncaoPublicaEstatica(nomeClasse, "new" + nomeClasse);
		funcao.addInstrucao(nomeClasse + " obj = new " + nomeClasse + "()");
		funcao.addInstrucao("obj.setId(1L)");
		funcao.addReturn("obj");

		arquivo.gerar(-1, pool);
		if (chkSetText.isSelected()) {
			setText(pool.toString());
		} else {
			appendText(pool.toString());
		}
	}

	@Override
	String getChaveTooltip() {
		return "label.test3_tooltip";
	}

	@Override
	String getChaveTitulo() {
		return "label.test3";
	}
}

class MetodoHandle {
	private final List<String> linhasArquivo;
	private final List<Metodo> metodos;

	public MetodoHandle(List<String> linhasArquivo) {
		this.linhasArquivo = Objects.requireNonNull(linhasArquivo);
		metodos = new ArrayList<>();
	}

	void processar(boolean get, boolean set, boolean is, boolean construtor, String classe, boolean outros) {
		for (String itemLinha : linhasArquivo) {
			String nome = Util.getNomeMetodo(itemLinha);
			if (nome != null) {
				Metodo metodo = new Metodo(nome);
				if (valido(metodo, get, set, is, construtor, classe, outros)) {
					metodos.add(metodo);
				}
			}
		}
	}

	private boolean valido(Metodo metodo, boolean get, boolean set, boolean is, boolean construtor, String classe,
			boolean outros) {
		if (metodo.isGet()) {
			return get;
		}
		if (metodo.isSet()) {
			return set;
		}
		if (metodo.isIs()) {
			return is;
		}
		if (metodo.isConstrutor(classe)) {
			return construtor;
		}
		return outros;
	}

	public List<Metodo> getMetodos() {
		return metodos;
	}
}