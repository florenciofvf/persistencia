package br.com.persist.plugins.navegacao;

import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.arquivo.ArquivoModelo;
import br.com.persist.arquivo.ArquivoTree;
import br.com.persist.arquivo.ArquivoTreeListener;
import br.com.persist.arquivo.ArquivoTreeUtil;
import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.Popup;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SplitPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.componente.TextField;
import br.com.persist.componente.ToolbarPesquisa;
import br.com.persist.data.Array;
import br.com.persist.data.ContainerDocument;
import br.com.persist.data.DataParser;
import br.com.persist.data.Filtro;
import br.com.persist.data.Objeto;
import br.com.persist.data.Texto;
import br.com.persist.data.Tipo;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLHandler;
import br.com.persist.marca.XMLUtil;
import br.com.persist.painel.Fichario;
import br.com.persist.painel.Root;
import br.com.persist.painel.Separador;
import br.com.persist.painel.SeparadorException;
import br.com.persist.painel.Transferivel;
import br.com.persist.plugins.instrucao.InstrucaoCor;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.InstrucaoMensagens;
import br.com.persist.plugins.instrucao.InstrucaoMetadados;
import br.com.persist.plugins.instrucao.MetaDialogoListener;
import br.com.persist.plugins.instrucao.biblionativo.HttpResult;
import br.com.persist.plugins.instrucao.biblionativo.HttpUtil;
import br.com.persist.plugins.instrucao.compilador.BibliotecaContexto;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.processador.CacheBiblioteca;
import br.com.persist.plugins.instrucao.processador.Processador;
import br.com.persist.plugins.objeto.ObjetoUtil;
import br.com.persist.plugins.requisicao.RequisicaoMensagens;
import br.com.persist.plugins.requisicao.visualizador.RequisicaoVisualizadorHeader;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

class NavegacaoSplit extends SplitPane {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	NavegacaoContainer container;
	private final File fileRoot;
	private ArquivoTree tree;
	private PanelRoot panel;

	NavegacaoSplit() {
		super(HORIZONTAL_SPLIT);
		fileRoot = new File(NavegacaoConstantes.NAVEGACOES);
	}

	void inicializar(NavegacaoContainer container) {
		this.container = container;
		File file = new File(fileRoot, NavegacaoConstantes.IGNORADOS);
		List<String> ignorados = ArquivoUtil.getIgnorados(file);
		ArquivoUtil.arquivoIgnorado(ignorados, NavegacaoPreferencia.isExibirArqIgnorados());
		Arquivo raiz = new Arquivo(fileRoot, ignorados);
		tree = new ArquivoTree(new ArquivoModelo(raiz));
		setLeftComponent(new ScrollPane(tree));
		tree.adicionarOuvinte(treeListener);
		panel = new PanelRoot();
		setRightComponent(panel);
		panel.tree = tree;
		abrir();
	}

	void salvar() throws XMLException {
		File file = new File(fileRoot, "hierarquia.xml");
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		util.abrirTag2("navegacao");
		panel.salvar(util);
		util.finalizarTag("navegacao");
		util.close();
	}

	void abrir() {
		File file = new File(fileRoot, "hierarquia.xml");
		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new NavegacaoHandler(panel, tree.getModelo()));
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	void abrir(Arquivo arquivo) throws NavegacaoException {
		if (arquivo == null) {
			return;
		}
		Fichario fichario = panel.getFicharioSelecionado();
		if (fichario != null) {
			novaAba(fichario, arquivo);
		} else {
			fichario = panel.getFicharioPrimeiro();
			if (fichario != null) {
				novaAba(fichario, arquivo);
			} else {
				fichario = novoFichario(arquivo);
				panel.setRoot(fichario);
			}
		}
		SwingUtilities.updateComponentTreeUI(panel);
	}

	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		tree.contemConteudo(set, string, porParte);
	}

	private Fichario novoFichario(Arquivo arquivo) {
		Fichario fichario = new Fichario();
		fichario.setTabPlacement(NavegacaoPreferencia.getNavegacaoPosicaoAbaFichario());
		novaAba(fichario, arquivo);
		return fichario;
	}

	public static void novaAba(Fichario fichario, Arquivo arquivo) {
		fichario.addTab(arquivo.getName(), new Aba(arquivo));
		int indice = fichario.getTabCount() - 1;
		fichario.setToolTipTextAt(indice, arquivo.getFile().getAbsolutePath());
		fichario.setSelectedIndex(indice);
	}

	public static void novaAba(Fichario fichario, File file) {
		fichario.addTab(file.getName(), new Aba(file));
		int indice = fichario.getTabCount() - 1;
		fichario.setToolTipTextAt(indice, file.getAbsolutePath());
		fichario.setSelectedIndex(indice);
	}

	public ArquivoTree getTree() {
		return tree;
	}

	private transient ArquivoTreeListener treeListener = new ArquivoTreeListener() {
		@Override
		public void focusInputPesquisar(ArquivoTree arquivoTree) {
			if (container != null) {
				container.focusInputPesquisarTree();
			}
		}

		@Override
		public void renomearArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null) {
				String nome = ArquivoUtil.getNome(NavegacaoSplit.this, arquivo.getName());
				if (nome != null && arquivo.renomear(nome)) {
					ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
					panel.renomear();
				}
			}
		}

		@Override
		public void excluirArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null && Util.confirmar(NavegacaoSplit.this, "msg.confirma_exclusao")) {
				arquivo.excluir();
				ArquivoTreeUtil.excluirEstrutura(arquivoTree, arquivo);
				try {
					panel.excluir(arquivo);
				} catch (NavegacaoException | SeparadorException ex) {
					Util.mensagem(NavegacaoSplit.this, ex.getMessage());
				}
			}
		}

		@Override
		public void clonarArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null) {
				clonar(arquivoTree, arquivo);
			}
		}

		private void clonar(ArquivoTree arquivoTree, Arquivo arquivo) {
			try {
				AtomicReference<File> ref = new AtomicReference<>();
				String resp = Util.clonar(NavegacaoSplit.this, arquivo.getFile(), ref);
				if (Preferencias.isExibirTotalBytesClonados()) {
					Util.mensagem(NavegacaoSplit.this, resp);
				}
				adicionar(arquivoTree, arquivo.getPai(), ref.get());
			} catch (IOException e) {
				Util.mensagem(NavegacaoSplit.this, e.getMessage());
			}
		}

		@Override
		public void diretorioArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null) {
				desktopOpen(arquivo);
			}
		}

		private void desktopOpen(Arquivo arquivo) {
			try {
				ArquivoUtil.diretorio(arquivo.getFile());
			} catch (IOException e) {
				Util.mensagem(NavegacaoSplit.this, e.getMessage());
			}
		}

		@Override
		public void abrirArquivo(ArquivoTree arquivoTree) {
			try {
				abrir(arquivoTree.getObjetoSelecionado());
			} catch (NavegacaoException ex) {
				Util.mensagem(NavegacaoSplit.this, ex.getMessage());
			}
		}

		@Override
		public void novoDiretorio(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoDiretorio(NavegacaoSplit.this, arquivo.getFile());
				adicionar(arquivoTree, arquivo, file);
			}
		}

		@Override
		public void novoArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoArquivo(NavegacaoSplit.this, arquivo.getFile());
				adicionar(arquivoTree, arquivo, file);
			}
		}

		private boolean valido(Arquivo arquivo) {
			return arquivo != null && arquivo.isDirectory();
		}

		private void adicionar(ArquivoTree arquivoTree, Arquivo arquivo, File file) {
			if (file != null && arquivo != null) {
				Arquivo novo = arquivo.adicionar(file);
				if (novo != null) {
					arquivo.ordenar();
					ArquivoTreeUtil.atualizarEstrutura(arquivoTree, arquivo);
					requestFocus();
					ArquivoTreeUtil.selecionarObjeto(arquivoTree, novo);
					arquivoTree.repaint();
				}
			}
		}
	};
}

class Editor extends TextEditor implements MetaDialogoListener {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;

	Editor() {
		addFocusListener(focusListenerInner);
		addKeyListener(keyListenerInner);
	}

	private transient FocusListener focusListenerInner = new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
			Component c = getParent();
			while (c != null) {
				if (c instanceof Fichario) {
					Fichario.setSelecionado((Fichario) c);
					break;
				}
				c = c.getParent();
			}
		}
	};

	private transient KeyListener keyListenerInner = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
				processar();
			}
		}

		private void processar() {
			Caret caret = getCaret();
			if (caret == null) {
				return;
			}
			int dot = caret.getDot() - 1;
			if (dot < 0) {
				return;
			}
			TextUI textUI = getUI();
			Rectangle r = null;
			try {
				r = textUI.modelToView(Editor.this, dot);
			} catch (BadLocationException ex) {
				return;
			}
			Point point = getLocationOnScreen();
			point.x += r.x + 5;
			point.y += r.y;
			String string = getString(dot);
			if (Util.isEmpty(string)) {
				return;
			}
			string = Util.trim(string, '.', false);
			string = Util.trim(string, '.', true);
			if (Util.isEmpty(string)) {
				return;
			}
			try {
				InstrucaoMetadados.abrir(Editor.this, string, Editor.this, point);
			} catch (InstrucaoException ex) {
				LOG.warning(ex.getMessage());
			}
		}

		private String getString(int dot) {
			StringBuilder sb = new StringBuilder();
			String string = getText();
			char c = string.charAt(dot);
			while (Compilador.valido3(c)) {
				sb.insert(0, c);
				dot--;
				if (dot >= 0) {
					c = string.charAt(dot);
				} else {
					break;
				}
			}
			return sb.toString();
		}
	};

	@Override
	public void setFragmento(String string) {
		Document doc = getDocument();
		if (doc != null) {
			try {
				int selectionEnd = getSelectionEnd();
				doc.insertString(selectionEnd, string, null);
			} catch (BadLocationException e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}
	}
}

class Aba extends Transferivel {
	private final PainelResultado painelResultado = new PainelResultado();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private transient BibliotecaContexto biblio;
	private final Editor editor = new Editor();
	final transient Arquivo arquivo;
	private ScrollPane scrollPane;

	Aba(Arquivo arquivo) {
		this.arquivo = Objects.requireNonNull(arquivo);
		toolbar.ini();
		montarLayout();
		abrir();
	}

	Aba(File file) {
		toolbar.ini(Mensagens.getString("msg.arquivo_inexistente") + " " + file.getAbsolutePath());
		add(BorderLayout.NORTH, toolbar);
		this.arquivo = null;
	}

	@Override
	public boolean associadoA(File file) {
		return arquivo.getFile().equals(file);
	}

	public static String getStringRelativo(File base, File file) {
		String absolutoBase = base.getAbsolutePath();
		String absolutoFile = file.getAbsolutePath();
		if (absolutoFile.startsWith(absolutoBase)) {
			int length = absolutoBase.length();
			String nome = absolutoFile.substring(length + 1);
			return Util.replaceAll(nome, Constantes.SEPARADOR, Constantes.SEP);
		}
		return file.getAbsolutePath();
	}

	@Override
	public String getStringFile() {
		return getStringRelativo(new File(NavegacaoConstantes.NAVEGACOES), arquivo.getFile());
	}

	@Override
	public File getFile() {
		return arquivo.getFile();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanel(), criarPanelResultado());
		SwingUtilities.invokeLater(() -> split.setResizeWeight(.5D));
		split.setOneTouchExpandable(true);
		split.setContinuousLayout(true);
		add(BorderLayout.CENTER, split);
		editor.setListener(
				TextEditor.newTextEditorAdapter(toolbar::focusInputPesquisar, toolbar::salvar, toolbar::baixar));
	}

	private Panel criarPanel() {
		Panel panel = new Panel();
		Panel panelArea = new Panel();
		panelArea.add(BorderLayout.CENTER, editor);
		scrollPane = new ScrollPane(panelArea);
		panel.add(BorderLayout.CENTER, scrollPane);
		scrollPane.setRowHeaderView(new TextEditorLine(editor));
		return panel;
	}

	private Panel criarPanelResultado() {
		Panel panel = new Panel();
		panel.add(BorderLayout.CENTER, painelResultado);
		return panel;
	}

	private int getValueScrollPane() {
		return scrollPane.getVerticalScrollBar().getValue();
	}

	private void setValueScrollPane(int value) {
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(value));
	}

	private class PainelResultado extends Panel {
		private final PopupFichario popupFichario = new PopupFichario();
		private final JTabbedPane fichario = new JTabbedPane();
		private static final long serialVersionUID = 1L;

		private PainelResultado() {
			fichario.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			fichario.addMouseListener(mouseListenerFichario);
			add(BorderLayout.CENTER, fichario);
		}

		private class PopupFichario extends Popup {
			private Action fechar = actionMenu("label.fechar");
			private static final long serialVersionUID = 1L;

			PopupFichario() {
				addMenuItem(fechar);
				fechar.setActionListener(e -> fechar());
			}

			private void fechar() {
				int indice = fichario.getSelectedIndex();
				if (indice != -1) {
					fichario.removeTabAt(indice);
				}
			}
		}

		private transient MouseListener mouseListenerFichario = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				processar(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				processar(e);
			}

			private void processar(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupFichario.show(fichario, e.getX(), e.getY());
				}
			}
		};

		private void setResposta(List<Object> resposta, boolean limpar) {
			if (NavegacaoUtil.isHttpResult(resposta)) {
				HttpResult result = (HttpResult) resposta.get(0);
				processar(result, limpar);
			} else {
				String string = ObjetoUtil.getStringResposta(resposta);
				setText(string, limpar);
			}
		}

		private void setText(String string, boolean limpar) {
			if (limpar) {
				fichario.removeAll();
			}
			addTab(new PainelDados(string));
		}

		private void processar(HttpResult result, boolean limpar) {
			if (limpar) {
				fichario.removeAll();
			}
			if (NavegacaoPreferencia.isExibirMetadados()) {
				addTab(new PainelMetadados(result));
			}
			observadores(result.getRequest(), result.getResponse());
		}

		private class PainelDados extends Panel implements IVisualizador {
			private static final long serialVersionUID = 1L;
			private TextEditor textEditor = new TextEditor();

			PainelDados(String string) {
				setText(string);

				ToolbarPesquisa toolbarPesquisa = new ToolbarPesquisa(textEditor);
				textEditor.setListener(TextEditor.newTextEditorAdapter(toolbarPesquisa::focusInputPesquisar));
				add(BorderLayout.NORTH, toolbarPesquisa);
				ScrollPane scrollPane2 = new ScrollPane(textEditor);
				scrollPane2.setRowHeaderView(new TextEditorLine(textEditor));
				Panel panelScroll = new Panel();
				panelScroll.add(BorderLayout.CENTER, scrollPane2);
				add(BorderLayout.CENTER, new ScrollPane(panelScroll));
			}

			private void setText(String string) {
				textEditor.setText(string);
				SwingUtilities.invokeLater(() -> textEditor.scrollRectToVisible(new Rectangle()));
			}

			@Override
			public String getTitulo() {
				return "Dados";
			}

			@Override
			public Icon getIcone() {
				return Icones.NOVO;
			}
		}

		private class PainelMetadados extends Panel implements IVisualizador {
			private static final long serialVersionUID = 1L;
			private TextEditor textEditor = new TextEditor();

			PainelMetadados(HttpResult result) {
				StringBuilder builder = new StringBuilder();
				info("Request", builder, result.getRequest());
				builder.append(Constantes.QL2);
				info("Response", builder, result.getResponse());
				setText(builder.toString());

				ToolbarPesquisa toolbarPesquisa = new ToolbarPesquisa(textEditor);
				textEditor.setListener(TextEditor.newTextEditorAdapter(toolbarPesquisa::focusInputPesquisar));
				add(BorderLayout.NORTH, toolbarPesquisa);
				ScrollPane scrollPane2 = new ScrollPane(textEditor);
				scrollPane2.setRowHeaderView(new TextEditorLine(textEditor));
				Panel panelScroll = new Panel();
				panelScroll.add(BorderLayout.CENTER, scrollPane2);
				add(BorderLayout.CENTER, new ScrollPane(panelScroll));
			}

			private void info(String titulo, StringBuilder builder, Map<String, Object> mapa) {
				builder.append(titulo + Constantes.QL);
				builder.append(Util.completar("", titulo.length(), '-') + Constantes.QL);
				append(0, builder, mapa);
			}

			@SuppressWarnings("unchecked")
			private void append(int tab, StringBuilder builder, Map<String, Object> mapa) {
				for (Map.Entry<String, Object> entry : mapa.entrySet()) {
					String chave = entry.getKey();
					Object valor = entry.getValue();
					if (valor instanceof Map) {
						builder.append(Util.completar("", tab, '\t') + chave + ":" + Constantes.QL);
						append(tab + 1, builder, (Map<String, Object>) valor);
					} else {
						builder.append(Util.completar("", tab, '\t') + chave + ": " + valor + Constantes.QL);
					}
				}
			}

			private void setText(String string) {
				textEditor.setText(string);
				SwingUtilities.invokeLater(() -> textEditor.scrollRectToVisible(new Rectangle()));
			}

			@Override
			public String getTitulo() {
				return "Metadados";
			}

			@Override
			public Icon getIcone() {
				return Icones.NOVO;
			}
		}

		private void observadores(Map<String, Object> mapaRequest, Map<String, Object> mapaResponse) {
			String location = NavegacaoUtil.getLocation(mapaResponse);
			String mimes = NavegacaoUtil.getMimes(mapaResponse);
			if (Util.isEmpty(mimes)) {
				return;
			}
			Object conteudo = mapaResponse.get("bytesResponse");
			if (conteudo instanceof byte[]) {
				byte[] bytes = (byte[]) conteudo;
				String string = new String(bytes);
				Cookie.processar(mapaResponse);
				notificar(getBase(mapaRequest.get("url")), location, mimes, bytes, string);
			}
		}

		private String getBase(Object obj) {
			if (obj == null) {
				return null;
			}
			String string = obj.toString();
			int pos = string.indexOf("://");
			if (pos == -1) {
				return null;
			}
			pos = string.indexOf("/", pos + 3);
			if (pos == -1) {
				return string;
			}
			return string.substring(0, pos);
		}

		private void notificar(String base, String location, String mimes, byte[] bytes, String string) {
			if (!Util.isEmpty(location)) {
				String complemento = null;
				if (location.startsWith("http://") || location.startsWith("https://")) {
					base = " ";
					complemento = location;
				} else {
					complemento = location.startsWith("/") ? location : "/" + location;
				}
				new InnerVisualizadorListener().processarLink(base, complemento);
				return;
			}
			if (NavegacaoPreferencia.isExibirConteudoPlano()) {
				addTab(new VisualizadorConteudo(bytes, string));
			}
			if (mimes.contains("image/")) {
				addTab(new VisualizadorImagem(bytes, string));
			}
			if (mimes.contains("text/html") || mimes.contains("text/javascript")) {
				VisualizadorHTML visualizador = new VisualizadorHTML(bytes, string);
				visualizador.visualizadorListener = new InnerVisualizadorListener();
				visualizador.base = base;
				addTab(visualizador);
			}
			if (mimes.contains("application/json")) {
				addTab(new VisualizadorJSON(bytes, string));
			}
			if (mimes.contains("application/pdf")) {
				addTab(new VisualizadorPDF(bytes, string));
			}
		}

		private void addTab(IVisualizador visualizador) {
			fichario.addTab(visualizador.getTitulo(), visualizador.getIcone(), (Component) visualizador);
			int ultimoIndice = fichario.getTabCount() - 1;
			fichario.setSelectedIndex(ultimoIndice);
		}

		private class InnerVisualizadorListener implements VisualizadorListener {
			@Override
			public void processarLink(String base, String complemento) {
				try {
					String aux = auxiliar(base, complemento);
					Processador processador = new Processador();
					List<Object> resposta = processador.processar("navegacao.processarLink", "main", base + aux,
							complemento);
					setResposta(resposta, false);
				} catch (InstrucaoException ex) {
					setText(Util.getStackTrace(NavegacaoConstantes.PAINEL_NAVEGACAO, ex), false);
				}
			}

			private String auxiliar(String base, String complemento) {
				if (base == null || complemento == null) {
					return "";
				}
				base = base.trim();
				complemento = complemento.trim();
				if (base.endsWith("/") || complemento.startsWith("/")) {
					return "";
				}
				return "/";
			}
		}
	}

	private void abrir() {
		editor.limpar();
		if (arquivo.getFile().exists()) {
			try {
				int value = getValueScrollPane();
				editor.setText(conteudo(arquivo.getFile()));
				setValueScrollPane(value);
				InstrucaoCor.clearAttr(editor.getStyledDocument());
				String texto = editor.getText().trim();
				if (texto.startsWith("/*abrir_compilar*/")) {
					SwingUtilities.invokeLater(toolbar::atualizar);
				}
				toolbar.executarAcao.setEnabled(!texto.startsWith("/*montar_arquivo*/"));
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, Aba.this);
			}
		}
	}

	public static String conteudo(File file) throws IOException {
		if (file != null && file.exists()) {
			StringBuilder sb = new StringBuilder();
			try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				int i = reader.read();
				while (i != -1) {
					sb.append((char) i);
					i = reader.read();
				}
			}
			return sb.toString();
		}
		return "";
	}

	@Override
	public void processar(Fichario fichario, int indice, Map<String, Object> map) {
		if (map.containsKey(Transferivel.RENOMEAR)) {
			fichario.setTitleAt(indice, arquivo.getName());
		}
	}

	private void executar() {
		toolbar.executar();
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action executarAnterioresAcao = acaoIcon("label.executar_anteriores", Icones.EXECUTAR);
		private Action vAccessTokenAcao = acaoMenu("label.atualizar_access_token_var");
		private Action executarAcao = acaoIcon("label.executar", Icones.EXECUTAR);
		private Action compiladoAcao = acaoIcon("label.compilado", Icones.ABRIR);
		private final CheckBox chkCertificados = new CheckBox();
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini() {
			super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR, SALVAR, ATUALIZAR);
			chkCertificados.addActionListener(e -> HttpUtil.setCertificados(!chkCertificados.isSelected()));
			chkCertificados.setToolTipText(Mensagens.getString("label.sem_certificados"));
			atualizarAcao.text(InstrucaoMensagens.getString("label.compilar_arquivo"));
			executarAnterioresAcao.setActionListener(e -> executarAnteriores());
			vAccessTokenAcao.setActionListener(e -> atualizarVar());
			compiladoAcao.setActionListener(e -> verCompilado());
			executarAcao.setActionListener(e -> executar());
			txtPesquisa.addActionListener(this);
			buttonColar.addSeparator();
			buttonColar.addItem(vAccessTokenAcao);
			addButton(compiladoAcao);
			add(chkCertificados);
			addButton(executarAnterioresAcao);
			addButton(executarAcao);
			add(txtPesquisa);
			add(label);
		}

		public void ini(String arqAbsoluto) {
			label.setText(arqAbsoluto);
			add(label);
		}

		Action acaoMenu(String chave) {
			return Action.acaoMenu(NavegacaoMensagens.getString(chave), null);
		}

		Action acaoIcon(String chave, Icon icon) {
			return Action.acaoIcon(InstrucaoMensagens.getString(chave), icon);
		}

		private void atualizarVar() {
			String string = Util.getContentTransfered();
			if (!Util.isEmpty(string)) {
				try {
					RequisicaoVisualizadorHeader.setAccesToken(string);
				} catch (ArgumentoException ex) {
					Util.mensagem(Aba.this, ex.getMessage());
				}
			}
		}

		private void verCompilado() {
			try {
				File file = Compilador.getCompilado(biblio);
				if (!file.exists()) {
					throw new IOException("Arquivo inexistente! " + file);
				}
				Util.conteudo(Aba.this, file, StandardCharsets.UTF_8);
			} catch (Exception e) {
				Util.mensagem(Aba.this, e.getMessage());
			}
		}

		private void executarAnteriores() {
			Fichario fichario = getFichario();
			if (fichario == null) {
				return;
			}
			List<Aba> abas = new ArrayList<>();
			for (int i = 0; i < fichario.getTabCount(); i++) {
				Component c = fichario.getComponent(i);
				if (c instanceof Aba) {
					abas.add((Aba) c);
				}
			}
			for (Aba item : abas) {
				item.executar();
			}
		}

		private Fichario getFichario() {
			Component c = this;
			while (c != null) {
				if (c instanceof Fichario) {
					return (Fichario) c;
				}
				c = c.getParent();
			}
			return null;
		}

		private void executar() {
			if (biblio == null) {
				painelResultado.setText(InstrucaoMensagens.getString("msg.nao_compilado"), true);
				return;
			}
			try {
				Processador processador = new Processador();
				List<Object> resposta = processador.processar(biblio.getNome(), "main");
				painelResultado.setResposta(resposta, true);
			} catch (InstrucaoException ex) {
				painelResultado.setText(Util.getStackTrace(NavegacaoConstantes.PAINEL_NAVEGACAO, ex), true);
			}
		}

		@Override
		protected void atualizar() {
			try {
				Compilador compilador = new Compilador();
				boolean colorir = false;
				if (editor.getText().trim().startsWith("/*montar_arquivo*/")) {
					biblio = compilador.compilar(criarArquivo(editor.getText()));
				} else {
					biblio = compilador.compilar(arquivo.getFile());
					colorir = true;
				}
				boolean resp = biblio != null;
				painelResultado.setText(resp ? InstrucaoMensagens.getString("msg.compilado")
						: InstrucaoMensagens.getString("msg.nao_compilado"), true);
				if (resp && colorir) {
					InstrucaoCor.processar(editor.getStyledDocument(), compilador.getTokens());
				}
			} catch (IOException | InstrucaoException ex) {
				painelResultado.setText(Util.getStackTrace(NavegacaoConstantes.PAINEL_NAVEGACAO, ex), true);
			}
		}

		@Override
		protected void baixar() {
			abrir();
			selecao = null;
			label.limpar();
		}

		@Override
		protected void limpar() {
			editor.limpar();
		}

		@Override
		protected void copiar() {
			String string = Util.getString(editor);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			editor.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(editor, numeros, letras);
		}

		@Override
		protected void salvar() {
			if (Util.confirmaSalvar(Aba.this, Constantes.TRES)) {
				salvarArquivo(arquivo.getFile());
			}
		}

		private void salvarArquivo(File file) {
			try {
				ArquivoUtil.salvar(editor, file);
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, Aba.this);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				selecao = Util.getSelecao(editor, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}

		private File criarArquivo(String string) throws IOException, InstrucaoException {
			List<String> nomes = listar(string, "gerar_arquivo{", "}");
			if (nomes.size() != 1) {
				throw new InstrucaoException("Erro no param arquivo{}. Total -> " + nomes.size(), false);
			}
			File file = CacheBiblioteca.arquivoParaCompilar(nomes.get(0));
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				List<String> arquivosIncluir = listar(string, "incluir{", "}");
				for (String strIncluir : arquivosIncluir) {
					File fileIncluir = CacheBiblioteca.arquivoParaCompilar(strIncluir);
					String fragmento = conteudo(fileIncluir);
					pw.write(fragmento);
				}
			}
			return file;
		}

		private List<String> listar(String string, String prefixo, String sufixo) {
			List<String> resp = new ArrayList<>();
			int pos = string.indexOf(prefixo);
			while (pos != -1) {
				int pos2 = string.indexOf(sufixo, pos + prefixo.length());
				if (pos2 != -1) {
					String fragmento = string.substring(pos + prefixo.length(), pos2);
					resp.add(fragmento.trim());
					pos = string.indexOf(prefixo, pos2 + sufixo.length());
				} else {
					break;
				}
			}
			return resp;
		}
	}
}

class PanelRoot extends Panel implements Root {
	private static final long serialVersionUID = 1L;
	ArquivoTree tree;

	void salvar(XMLUtil util) {
		if (getComponentCount() == 0) {
			return;
		}
		if (getComponent(0) instanceof Fichario) {
			((Fichario) getComponent(0)).salvar(util);
		}
		if (getComponent(0) instanceof Separador) {
			((Separador) getComponent(0)).salvar(util);
		}
	}

	Fichario getFicharioSelecionado() {
		if (getComponentCount() == 0) {
			return null;
		}
		if (getComponent(0) instanceof Fichario) {
			return (Fichario) getComponent(0);
		}
		return ((Separador) getComponent(0)).getFicharioSelecionado();
	}

	Fichario getFicharioPrimeiro() {
		if (getComponentCount() == 0) {
			return null;
		}
		if (getComponent(0) instanceof Fichario) {
			return (Fichario) getComponent(0);
		}
		return ((Separador) getComponent(0)).getFicharioPrimeiro();
	}

	Transferivel getTransferivel(File file) {
		if (getComponentCount() == 0) {
			return null;
		}
		if (getComponent(0) instanceof Fichario) {
			return ((Fichario) getComponent(0)).getTransferivel(file);
		}
		return ((Separador) getComponent(0)).getTransferivel(file);
	}

	Fichario getFichario(Transferivel objeto) {
		if (getComponentCount() == 0) {
			return null;
		}
		if (getComponent(0) instanceof Fichario && ((Fichario) getComponent(0)).contem(objeto)) {
			return (Fichario) getComponent(0);
		}
		if (getComponent(0) instanceof Separador) {
			return ((Separador) getComponent(0)).getFichario(objeto);
		}
		return null;
	}

	void setRootIf(Component c) {
		if (getComponentCount() == 0) {
			add(c);
		}
	}

	void setRoot(Component c) throws NavegacaoException {
		if (getComponentCount() > 0) {
			throw new NavegacaoException("getComponentCount() > 0", false);
		}
		add(c);
	}

	void renomear() {
		if (getComponentCount() == 0) {
			return;
		}
		Map<String, Object> map = new HashMap<>();
		map.put(Transferivel.RENOMEAR, null);
		if (getComponent(0) instanceof Fichario) {
			((Fichario) getComponent(0)).processar(map);
			return;
		}
		((Separador) getComponent(0)).processar(map);
	}

	void excluir(Arquivo arquivo) throws NavegacaoException, SeparadorException {
		Transferivel objeto = getTransferivel(arquivo.getFile());
		while (objeto != null) {
			Fichario fichario = getFichario(objeto);
			if (fichario != null) {
				if (!fichario.contem(objeto)) {
					throw new NavegacaoException("!fichario.contem(objeto)", false);
				} else {
					fichario.excluir(objeto);
				}
			} else {
				throw new NavegacaoException("fichario == null", false);
			}
			objeto = getTransferivel(arquivo.getFile());
		}
	}

	@Override
	public void abaSelecionada(Fichario fichario, Transferivel transferivel) {
		if (transferivel instanceof Aba) {
			Aba aba = (Aba) transferivel;
			if (aba.arquivo != null) {
				ArquivoTreeUtil.selecionarObjeto(tree, aba.arquivo);
			}
		}
	}
}

class NavegacaoHandler extends XMLHandler {
	private final ArquivoModelo modelo;
	private final PanelRoot root;
	Separador separador;
	Fichario fichario;

	NavegacaoHandler(PanelRoot root, ArquivoModelo modelo) {
		this.modelo = modelo;
		this.root = root;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("fichario".equals(qName)) {
			fichario = new Fichario();
			fichario.setTabPlacement(NavegacaoPreferencia.getNavegacaoPosicaoAbaFichario());
			root.setRootIf(fichario);
			setComponent(separador, fichario);
		} else if ("separador".equals(qName)) {
			int orientation = Integer.parseInt(attributes.getValue("orientacao"));
			Separador separadorBkp = separador;
			separador = new Separador(orientation, null, null);
			root.setRootIf(separador);
			setComponent(separadorBkp, separador);
		} else if ("transferivel".equals(qName)) {
			String nome = attributes.getValue("file");
			nome = Util.replaceAll(nome, Constantes.SEP, Constantes.SEPARADOR);
			File fileRoot = new File(NavegacaoConstantes.NAVEGACOES);
			File file = new File(fileRoot, nome);
			Arquivo arquivo = modelo.getArquivo(file);
			if (arquivo != null) {
				NavegacaoSplit.novaAba(fichario, arquivo);
			} else {
				NavegacaoSplit.novaAba(fichario, file);
			}
		}
	}

	private void setComponent(Separador separador, Component c) {
		if (separador != null) {
			if (separador.getLeftComponent() == null) {
				separador.setLeftComponent(c);
			} else {
				separador.setRightComponent(c);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("fichario".equals(qName)) {
			if (fichario.getParent() instanceof Separador) {
				separador = (Separador) fichario.getParent();
			}
			fichario = null;
		} else if ("separador".equals(qName)) {
			if (separador.getParent() instanceof Separador) {
				separador = (Separador) separador.getParent();
			}
			fichario = null;
		}
	}
}

class Cookie {
	private Cookie() {
	}

	static void processar(Map<String, Object> mapa) {
		String valor = NavegacaoUtil.getCookie(mapa);
		if (Util.isEmpty(valor)) {
			return;
		}
		final String NOME_VAR = "VAR_COOKIE";
		Variavel v = VariavelProvedor.getVariavel(NOME_VAR);
		if (v == null) {
			try {
				VariavelProvedor.adicionar(NOME_VAR, valor);
			} catch (ArgumentoException e) {
				//
			}
		} else {
			v.setValor(valor);
		}
	}
}

class AuthenticityToken {
	private AuthenticityToken() {
	}

	static void processar(String string) {
		if (Util.isEmpty(string)) {
			return;
		}
		String str = "name=\"authenticity_token\" value=\"";
		int pos = string.indexOf(str);
		if (pos == -1) {
			return;
		}
		int pos2 = string.indexOf('"', pos + str.length() + 1);
		String valor = string.substring(pos + str.length(), pos2);
		final String NOME_VAR = "VAR_AUTHENTICITY_TOKEN";
		Variavel v = VariavelProvedor.getVariavel(NOME_VAR);
		if (v == null) {
			try {
				VariavelProvedor.adicionar(NOME_VAR, valor);
			} catch (ArgumentoException e) {
				//
			}
		} else {
			v.setValor(valor);
		}
	}
}

class AccessToken {
	private AccessToken() {
	}

	static void processar(Tipo json) {
		String accessToken = getAccessToken(json);
		salvarAccessToken(accessToken);
	}

	private static String getAccessToken(Tipo tipo) {
		if (tipo instanceof Objeto) {
			Objeto objeto = (Objeto) tipo;
			Tipo tipoAccessToken = objeto.getValor("access_token");
			return tipoAccessToken instanceof Texto ? tipoAccessToken.toString() : null;
		}
		return null;
	}

	private static void salvarAccessToken(String valor) {
		if (Util.isEmpty(valor)) {
			return;
		}
		final String NOME_VAR = "VAR_ACCESS_TOKEN";
		Variavel v = VariavelProvedor.getVariavel(NOME_VAR);
		if (v == null) {
			try {
				VariavelProvedor.adicionar(NOME_VAR, valor);
			} catch (ArgumentoException e) {
				//
			}
		} else {
			v.setValor(valor);
		}
	}
}

interface IVisualizador {
	public String getTitulo();

	public Icon getIcone();
}

interface VisualizadorListener {
	public void processarLink(String base, String complemento);
}

abstract class Visualizador extends Panel implements IVisualizador {
	protected transient VisualizadorListener visualizadorListener;
	private static final long serialVersionUID = 1L;
	protected final String string;
	protected final byte[] bytes;
	protected String base;

	protected Visualizador(byte[] bytes, String string) {
		this.string = string;
		this.bytes = bytes;
	}

	protected BarraButton criarToolbarPesquisa(JTextPane textPane, TextEditor textEditor) {
		ToolbarPesquisa toolbarPesquisa = new ToolbarPesquisa(textPane);
		if (textEditor != null) {
			textEditor.setListener(TextEditor.newTextEditorAdapter(toolbarPesquisa::focusInputPesquisar));
		}
		return toolbarPesquisa;
	}
}

class VisualizadorImagem extends Visualizador {
	private static final long serialVersionUID = 1L;

	protected VisualizadorImagem(byte[] bytes, String string) {
		super(bytes, string);

		Label label = new Label();
		label.setIcon(new ImageIcon(bytes));

		add(BorderLayout.CENTER, new ScrollPane(label));
		SwingUtilities.invokeLater(() -> label.scrollRectToVisible(new Rectangle()));
	}

	@Override
	public String getTitulo() {
		return "Imagem";
	}

	@Override
	public Icon getIcone() {
		return Icones.ICON;
	}
}

class VisualizadorConteudo extends Visualizador {
	private static final long serialVersionUID = 1L;

	protected VisualizadorConteudo(byte[] bytes, String string) {
		super(bytes, string);

		JTextPane textPane = new JTextPane();
		textPane.setText(string);

		add(BorderLayout.NORTH, criarToolbarPesquisa(textPane, null));
		add(BorderLayout.CENTER, new ScrollPane(textPane));
		SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));
	}

	@Override
	public String getTitulo() {
		return NavegacaoMensagens.getString("label.conteudo");
	}

	@Override
	public Icon getIcone() {
		return Icones.TEXTO;
	}
}

class VisualizadorPDF extends Visualizador {
	private static final long serialVersionUID = 1L;

	protected VisualizadorPDF(byte[] bytes, String string) {
		super(bytes, string);
		try {
			Class<?> klass = Class.forName("com.qoppa.pdfViewer.PDFViewerBean");
			Object objeto = klass.newInstance();
			JComponent comp = (JComponent) objeto;
			load(klass, objeto, bytes);
			add(BorderLayout.CENTER, comp);
			SwingUtilities.invokeLater(() -> comp.scrollRectToVisible(new Rectangle()));
		} catch (Exception e) {
			//
		}
	}

	private void load(Class<?> klass, Object objeto, byte[] bytes)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method method = klass.getDeclaredMethod("loadPDF", InputStream.class);
		method.invoke(objeto, new ByteArrayInputStream(bytes));
	}

	@Override
	public String getTitulo() {
		return "PDF";
	}

	@Override
	public Icon getIcone() {
		return Icones.PDF;
	}
}

class VisualizadorHTML extends Visualizador {
	private static final long serialVersionUID = 1L;

	protected VisualizadorHTML(byte[] bytes, String string) {
		super(bytes, string);

		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.addHyperlinkListener(new Listener());
		textPane.setContentType("text/html");
		textPane.setText(string);

		AuthenticityToken.processar(string);

		Panel panelTextPane = new Panel();
		panelTextPane.add(BorderLayout.CENTER, textPane);

		add(BorderLayout.NORTH, criarToolbarPesquisa(textPane, null));
		add(BorderLayout.CENTER, new ScrollPane(panelTextPane));
		SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));
	}

	private class Listener implements HyperlinkListener {
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				JEditorPane pane = (JEditorPane) e.getSource();
				URL url = e.getURL();
				if (url != null) {
					setPage(pane, url);
					return;
				}
				String desc = e.getDescription();
				if (!Util.isEmpty(base) && !Util.isEmpty(desc) && visualizadorListener != null) {
					visualizadorListener.processarLink(base, desc);
				}
			}
		}

		private void setPage(JEditorPane pane, URL url) {
			try {
				pane.setPage(url);
			} catch (Exception ex) {
				//
			}
		}
	}

	@Override
	public String getTitulo() {
		return "HTML";
	}

	@Override
	public Icon getIcone() {
		return Icones.URL;
	}
}

class VisualizadorJSON extends Visualizador {
	private final transient DataParser parser = new DataParser();
	private static final long serialVersionUID = 1L;

	protected VisualizadorJSON(byte[] bytes, String string) {
		super(bytes, string);
		try {
			JTextPane textPane = new JTextPane();
			Tipo json = parser.parse(string);
			setText(json, textPane);

			AccessToken.processar(json);

			Panel panelTextPane = new Panel();
			panelTextPane.add(BorderLayout.CENTER, textPane);

			BarraButton barraButton = criarToolbarPesquisa(textPane, null);
			config(barraButton, json, textPane);

			add(BorderLayout.NORTH, barraButton);
			add(BorderLayout.CENTER, new ScrollPane(panelTextPane));
			SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));
		} catch (Exception e) {
			//
		}
	}

	private void setText(Tipo json, JTextPane textPane) {
		textPane.setText(Constantes.VAZIO);
		StyledDocument styledDoc = textPane.getStyledDocument();
		if (styledDoc instanceof AbstractDocument && json != null) {
			AbstractDocument doc = (AbstractDocument) styledDoc;
			json.export(new ContainerDocument(doc), 0);
		}
	}

	private void config(BarraButton barraButton, Tipo json, JTextPane textPane) {
		Action totalElemAction = Action.acaoIcon(RequisicaoMensagens.getString("label.total_elementos"), Icones.INFO);
		Action comAtributoAction = Action.acaoMenu(RequisicaoMensagens.getString("label.com_atributos"),
				Icones.EXECUTAR);
		Action semAtributoAction = Action.acaoMenu(RequisicaoMensagens.getString("label.sem_atributos"),
				Icones.EXECUTAR);
		Action originalAction = Action.acaoMenu(RequisicaoMensagens.getString("label.original"), Icones.EXECUTAR);
		TextField txtComAtributo = new TextField(15);
		TextField txtVlrAtributo = new TextField(15);
		TextField txtSemAtributo = new TextField(15);

		comAtributoAction.setActionListener(e -> filtrarComAtributo(json, textPane, txtComAtributo, txtVlrAtributo));
		semAtributoAction.setActionListener(e -> filtrarSemAtributo(json, textPane, txtSemAtributo));
		txtVlrAtributo.setToolTipText(NavegacaoMensagens.getString("label.valor_atributo"));
		totalElemAction.setActionListener(e -> totalElementos(textPane));
		originalAction.setActionListener(e -> retornar(json, textPane));

		barraButton.addButton(comAtributoAction);
		barraButton.add(txtComAtributo);
		barraButton.add(txtVlrAtributo);
		barraButton.addButton(semAtributoAction);
		barraButton.add(txtSemAtributo);
		barraButton.addButton(originalAction);
		barraButton.addButton(totalElemAction);
	}

	private void totalElementos(JTextPane textPane) {
		if (!Util.isEmpty(textPane.getText())) {
			try {
				Tipo json = parser.parse(textPane.getText());
				if (json instanceof Array) {
					String msg = RequisicaoMensagens.getString("label.total_elementos");
					Util.mensagem(textPane, msg + " [" + ((Array) json).getElementos().size() + "]");
				} else {
					Util.mensagem(textPane, RequisicaoMensagens.getString("msg.objeto_principal_nao_array"));
				}
			} catch (Exception e) {
				Util.mensagem(textPane, e.getMessage());
			}
		}
	}

	private void filtrarComAtributo(Tipo json, JTextPane textPane, TextField textField,
			TextField textFieldValorAtributo) {
		if ((json instanceof Objeto || json instanceof Array) && !Util.isEmpty(textField.getText())) {
			String[] atributos = textField.getText().split(",");
			filtrarComAtributos(json.clonar(), atributos, textFieldValorAtributo.getText(), textPane);
		}
	}

	private void filtrarSemAtributo(Tipo json, JTextPane textPane, TextField textField) {
		if ((json instanceof Objeto || json instanceof Array) && !Util.isEmpty(textField.getText())) {
			String[] atributos = textField.getText().split(",");
			filtrarSemAtributos(json.clonar(), atributos, textPane);
		}
	}

	private void retornar(Tipo json, JTextPane textPane) {
		setText(json, textPane);
	}

	private void filtrarComAtributos(Tipo json, String[] atributos, String valorAtributo, JTextPane textPane) {
		if (json instanceof Objeto) {
			json = Filtro.comAtributos((Objeto) json, atributos, valorAtributo);
		} else if (json instanceof Array) {
			json = Filtro.comAtributos((Array) json, atributos, valorAtributo);
		}
		setText(json, textPane);
	}

	private void filtrarSemAtributos(Tipo json, String[] atributos, JTextPane textPane) {
		if (json instanceof Objeto) {
			json = Filtro.semAtributos((Objeto) json, atributos);
		} else if (json instanceof Array) {
			json = Filtro.semAtributos((Array) json, atributos);
		}
		setText(json, textPane);
	}

	@Override
	public String getTitulo() {
		return "JSON";
	}

	@Override
	public Icon getIcone() {
		return Icones.CONFIG;
	}
}