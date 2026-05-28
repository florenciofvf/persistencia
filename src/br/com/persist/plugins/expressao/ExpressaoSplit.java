package br.com.persist.plugins.expressao;

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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.arquivo.ArquivoModelo;
import br.com.persist.arquivo.ArquivoTree;
import br.com.persist.arquivo.ArquivoTreeListener;
import br.com.persist.arquivo.ArquivoTreeUtil;
import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SplitPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.componente.ToolbarPesquisa;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLHandler;
import br.com.persist.marca.XMLUtil;
import br.com.persist.painel.Fichario;
import br.com.persist.painel.Root;
import br.com.persist.painel.Separador;
import br.com.persist.painel.SeparadorException;
import br.com.persist.painel.Transferivel;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.BibliotecaContexto;
import br.com.persist.plugins.expressao.biblioteca.CacheBiblioteca;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.processador.Processador;

class ExpressaoSplit extends SplitPane {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	ExpressaoContainer container;
	private final File fileRoot;
	private ArquivoTree tree;
	private PanelRoot panel;

	ExpressaoSplit() {
		super(HORIZONTAL_SPLIT);
		fileRoot = new File(ExpressaoConstantes.EXPRESSOES);
	}

	void inicializar(ExpressaoContainer container) {
		this.container = container;
		File file = new File(fileRoot, ExpressaoConstantes.IGNORADOS);
		List<String> ignorados = ArquivoUtil.getIgnorados(file);
		ArquivoUtil.arquivoIgnorado(ignorados, ExpressaoPreferencia.isExibirArqIgnorados());
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
		util.abrirTag2("expressao");
		panel.salvar(util);
		util.finalizarTag("expressao");
		util.close();
	}

	void abrir() {
		File file = new File(fileRoot, "hierarquia.xml");
		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new ExpressaoHandler(panel, tree.getModelo()));
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	void abrir(Arquivo arquivo) throws ExpressaoException {
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
		fichario.setTabPlacement(ExpressaoPreferencia.getExpressaoPosicaoAbaFichario());
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
				String nome = ArquivoUtil.getNome(ExpressaoSplit.this, arquivo.getName());
				if (nome != null && arquivo.renomear(nome)) {
					ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
					panel.renomear();
				}
			}
		}

		@Override
		public void excluirArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null && Util.confirmar(ExpressaoSplit.this, "msg.confirma_exclusao")) {
				arquivo.excluir();
				ArquivoTreeUtil.excluirEstrutura(arquivoTree, arquivo);
				try {
					panel.excluir(arquivo);
				} catch (ExpressaoException | SeparadorException ex) {
					Util.mensagem(ExpressaoSplit.this, ex.getMessage());
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
				String resp = Util.clonar(ExpressaoSplit.this, arquivo.getFile(), ref);
				if (Preferencias.isExibirTotalBytesClonados()) {
					Util.mensagem(ExpressaoSplit.this, resp);
				}
				adicionar(arquivoTree, arquivo.getPai(), ref.get());
			} catch (IOException e) {
				Util.mensagem(ExpressaoSplit.this, e.getMessage());
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
				Util.mensagem(ExpressaoSplit.this, e.getMessage());
			}
		}

		@Override
		public void abrirArquivo(ArquivoTree arquivoTree) {
			try {
				abrir(arquivoTree.getObjetoSelecionado());
			} catch (ExpressaoException ex) {
				Util.mensagem(ExpressaoSplit.this, ex.getMessage());
			}
		}

		@Override
		public void novoDiretorio(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoDiretorio(ExpressaoSplit.this, arquivo.getFile());
				adicionar(arquivoTree, arquivo, file);
			}
		}

		@Override
		public void novoArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoArquivo(ExpressaoSplit.this, arquivo.getFile());
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

class Editor extends TextEditor {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	transient BibliotecaContexto bibliotecaContexto;
	transient javax.swing.Action compilarAction;
	transient CacheBiblioteca cacheBiblioteca;
	private Point point;

	Editor() {
		inputMap().put(getKeyStrokeCtrl(KeyEvent.VK_P), "compilar");
		getActionMap().put("compilar", actionCompilar);
		addFocusListener(focusListenerInner);
		addKeyListener(keyListenerInner);
	}

	private transient javax.swing.Action actionCompilar = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (compilarAction != null) {
				compilarAction.actionPerformed(e);
			}
		}
	};

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
				processar(false);
			} else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_SPACE) {
				processar(true);
			}
		}

		private void processar(boolean local) {
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
			point = getLocationOnScreen();
			point.x += r.x + 5;
			point.y += r.y;
			if (local) {
				processarLocal();
				return;
			}
			String string = getString(dot);
			if (Util.isEmpty(string)) {
				return;
			}
			String[] array = ExpressaoUtil.getArray(string);
			if (array.length == 1) {
				processarAlias(array[0]);
			} else if (array.length > 2) {
				processarBiblio(ExpressaoUtil.get(array));
			}
		}

		private void processarLocal() {
			if (bibliotecaContexto == null) {
				LOG.warning(ExpressaoMensagens.getString("erro.compile_arquivo_alias"));
				return;
			}
			if (cacheBiblioteca != null) {
				cacheBiblioteca.clear();
			}
			try {
				processarBiblio(bibliotecaContexto.getNomeAbsoluto());
			} catch (ExpressaoException ex) {
				LOG.warning(ex.getMessage());
			}
		}

		private String getString(int dot) {
			StringBuilder sb = new StringBuilder();
			String string = getText();
			char c = string.charAt(dot);
			while (TokenManager.valido3(c)) {
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

		private void processarAlias(String alias) {
			if (bibliotecaContexto == null) {
				LOG.warning(ExpressaoMensagens.getString("erro.compile_arquivo_alias"));
				return;
			}
			try {
				String biblioteca = bibliotecaContexto.getAlias().get(alias);
				processarBiblio(biblioteca);
			} catch (ExpressaoException ex) {
				LOG.warning(ex.getMessage());
			}
		}

		private void processarBiblio(String biblio) {
			if (cacheBiblioteca == null || biblio == null) {
				return;
			}
			Biblioteca biblioteca = null;
			try {
				biblioteca = cacheBiblioteca.getBiblioteca(biblio);
			} catch (ExpressaoException ex) {
				LOG.warning(ex.getMessage());
				return;
			}
			ExpressaoMetadados.abrir(Editor.this, biblioteca, metaDialogoListener, point);
		}
	};

	private transient MetaDialogoListener metaDialogoListener = new MetaDialogoListener() {
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

		@Override
		public String getDestino() {
			return "Editor";
		}
	};
}

class Aba extends Transferivel {
	private transient CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
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
		return getStringRelativo(new File(ExpressaoConstantes.EXPRESSOES), arquivo.getFile());
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
		editor.compilarAction = new CompilarAction();
		editor.cacheBiblioteca = cacheBiblioteca;
	}

	private class CompilarAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			toolbar.atualizar();
		}
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
		private TextEditor textEditor = new TextEditor();
		private static final long serialVersionUID = 1L;

		private PainelResultado() {
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
	}

	private void abrir() {
		editor.limpar();
		if (arquivo.getFile().exists()) {
			try {
				int value = getValueScrollPane();
				editor.setText(Compilacao.conteudo(arquivo.getFile()));
				setValueScrollPane(value);
				ExpressaoCor.clearAttr(editor.getStyledDocument());
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

	@Override
	public void processar(Fichario fichario, int indice, Map<String, Object> map) {
		if (map.containsKey(Transferivel.RENOMEAR)) {
			fichario.setTitleAt(indice, arquivo.getName());
		}
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action executarAcao = acaoIcon("label.executar", Icones.EXECUTAR);
		private Action compiladoAcao = acaoIcon("label.compilado", Icones.ABRIR);
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini() {
			super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR, SALVAR, ATUALIZAR);
			atualizarAcao.text(ExpressaoMensagens.getString("label.compilar_arquivo"));
			compiladoAcao.setActionListener(e -> verCompilado());
			executarAcao.setActionListener(e -> executar());
			txtPesquisa.addActionListener(this);
			addButton(compiladoAcao);
			addButton(executarAcao);
			add(txtPesquisa);
			add(label);
		}

		public void ini(String arqAbsoluto) {
			label.setText(arqAbsoluto);
			add(label);
		}

		Action acaoIcon(String chave, Icon icon) {
			return Action.acaoIcon(ExpressaoMensagens.getString(chave), icon);
		}

		private void verCompilado() {
			try {
				File file = Compilacao.getCompilado(biblio);
				if (!file.exists()) {
					throw new IOException("Arquivo inexistente! " + file);
				}
				Util.conteudo(Aba.this, file, StandardCharsets.UTF_8);
			} catch (Exception e) {
				Util.mensagem(Aba.this, e.getMessage());
			}
		}

		private void executar() {
			if (biblio == null) {
				painelResultado.setText(ExpressaoMensagens.getString("msg.nao_compilado"));
				return;
			}
			try {
				Processador processador = new Processador();
				List<Object> resposta = processador.processar(biblio.getNomeAbsoluto(), "main");
				painelResultado.setText(resposta.toString());
			} catch (ExpressaoException ex) {
				painelResultado.setText(Util.getStackTrace(ExpressaoConstantes.PAINEL_EXPRESSAO, ex));
			}
		}

		@Override
		protected void atualizar() {
			try {
				Compilacao compilacao = new Compilacao();
				boolean colorir = false;
				if (editor.getText().trim().startsWith("/*montar_arquivo*/")) {
					biblio = compilacao.compilar(criarArquivo(editor.getText()));
				} else {
					biblio = compilacao.compilar(arquivo.getFile());
					colorir = true;
				}
				boolean resp = biblio != null;
				editor.bibliotecaContexto = biblio;
				painelResultado
						.setText(resp ? ExpressaoMensagens.getString("msg.compilado") + compilacao.getStringAlerta()
								: ExpressaoMensagens.getString("msg.nao_compilado"));
				if (resp && colorir) {
					ExpressaoCor.processar(editor.getStyledDocument(), compilacao.getTokens());
				}
			} catch (IOException | ExpressaoException ex) {
				painelResultado.setText(Util.getStackTrace(ExpressaoConstantes.PAINEL_EXPRESSAO, ex));
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
			if (Util.confirmaSalvar(Aba.this)) {
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

		private File criarArquivo(String string) throws IOException, ExpressaoException {
			List<String> nomes = listar(string, "gerar_arquivo{", "}");
			if (nomes.size() != 1) {
				throw new ExpressaoException("Erro no param arquivo{}. Total -> " + nomes.size(), false);
			}
			File file = CacheBiblioteca.arquivoParaCompilar(nomes.get(0));
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				List<String> arquivosIncluir = listar(string, "incluir{", "}");
				for (String strIncluir : arquivosIncluir) {
					File fileIncluir = CacheBiblioteca.arquivoParaCompilar(strIncluir);
					String fragmento = Compilacao.conteudo(fileIncluir);
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

	void setRoot(Component c) throws ExpressaoException {
		if (getComponentCount() > 0) {
			throw new ExpressaoException("getComponentCount() > 0", false);
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

	void excluir(Arquivo arquivo) throws ExpressaoException, SeparadorException {
		Transferivel objeto = getTransferivel(arquivo.getFile());
		while (objeto != null) {
			Fichario fichario = getFichario(objeto);
			if (fichario != null) {
				if (!fichario.contem(objeto)) {
					throw new ExpressaoException("!fichario.contem(objeto)", false);
				} else {
					fichario.excluir(objeto);
				}
			} else {
				throw new ExpressaoException("fichario == null", false);
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

class ExpressaoHandler extends XMLHandler {
	private final ArquivoModelo modelo;
	private final PanelRoot root;
	Separador separador;
	Fichario fichario;

	ExpressaoHandler(PanelRoot root, ArquivoModelo modelo) {
		this.modelo = modelo;
		this.root = root;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("fichario".equals(qName)) {
			fichario = new Fichario();
			fichario.setTabPlacement(ExpressaoPreferencia.getExpressaoPosicaoAbaFichario());
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
			File fileRoot = new File(ExpressaoConstantes.EXPRESSOES);
			File file = new File(fileRoot, nome);
			Arquivo arquivo = modelo.getArquivo(file);
			if (arquivo != null) {
				ExpressaoSplit.novaAba(fichario, arquivo);
			} else {
				ExpressaoSplit.novaAba(fichario, file);
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