package br.com.persist.plugins.instrucao;

import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.arquivo.ArquivoModelo;
import br.com.persist.arquivo.ArquivoTree;
import br.com.persist.arquivo.ArquivoTreeListener;
import br.com.persist.arquivo.ArquivoTreeUtil;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SplitPane;
import br.com.persist.componente.TextField;
import br.com.persist.componente.TextPane;
import br.com.persist.componente.ToolbarPesquisa;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLHandler;
import br.com.persist.marca.XMLUtil;
import br.com.persist.painel.Fichario;
import br.com.persist.painel.Separador;
import br.com.persist.painel.SeparadorException;
import br.com.persist.painel.Transferivel;
import br.com.persist.plugins.instrucao.compilador.BibliotecaContexto;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Token;
import br.com.persist.plugins.instrucao.processador.Processador;

class InstrucaoSplit extends SplitPane {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final File fileRoot;
	private ArquivoTree tree;
	private PanelRoot panel;

	InstrucaoSplit() {
		super(HORIZONTAL_SPLIT);
		fileRoot = new File(InstrucaoConstantes.INSTRUCAO);
	}

	void inicializar() {
		File file = new File(fileRoot, InstrucaoConstantes.IGNORADOS);
		List<String> ignorados = ArquivoUtil.getIgnorados(file);
		Arquivo raiz = new Arquivo(fileRoot, ignorados);
		tree = new ArquivoTree(new ArquivoModelo(raiz));
		setLeftComponent(new ScrollPane(tree));
		tree.adicionarOuvinte(treeListener);
		panel = new PanelRoot();
		setRightComponent(panel);
		abrir();
	}

	void salvar() throws XMLException {
		File file = new File(fileRoot, "hierarquia.xml");
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		util.abrirTag2("instrucoes");
		panel.salvar(util);
		util.finalizarTag("instrucoes");
		util.close();
	}

	void abrir() {
		File file = new File(fileRoot, "hierarquia.xml");
		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new AnotacaoHandler(panel, tree.getModelo()));
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	void abrir(Arquivo arquivo) throws InstrucaoException {
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

	public void contemConteudo(Set<String> set, String string) {
		tree.contemConteudo(set, string);
	}

	private Fichario novoFichario(Arquivo arquivo) {
		Fichario fichario = new Fichario();
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
		public void renomearArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null) {
				String nome = ArquivoUtil.getNome(InstrucaoSplit.this, arquivo.getName());
				if (nome != null && arquivo.renomear(nome)) {
					ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
					panel.renomear();
				}
			}
		}

		@Override
		public void excluirArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null && Util.confirmar(InstrucaoSplit.this, "msg.confirma_exclusao")) {
				arquivo.excluir();
				ArquivoTreeUtil.excluirEstrutura(arquivoTree, arquivo);
				try {
					panel.excluir(arquivo);
				} catch (InstrucaoException | SeparadorException ex) {
					Util.mensagem(InstrucaoSplit.this, ex.getMessage());
				}
			}
		}

		@Override
		public void clonarArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null) {
				clonar(arquivo);
			}
		}

		private void clonar(Arquivo arquivo) {
			try {
				String resp = Util.clonar(arquivo.getFile());
				Util.mensagem(InstrucaoSplit.this, resp);
			} catch (IOException e) {
				Util.mensagem(InstrucaoSplit.this, e.getMessage());
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
				Util.mensagem(InstrucaoSplit.this, e.getMessage());
			}
		}

		@Override
		public void abrirArquivo(ArquivoTree arquivoTree) {
			try {
				abrir(arquivoTree.getObjetoSelecionado());
			} catch (InstrucaoException ex) {
				Util.mensagem(InstrucaoSplit.this, ex.getMessage());
			}
		}

		@Override
		public void novoDiretorio(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoDiretorio(InstrucaoSplit.this, arquivo.getFile());
				adicionar(arquivoTree, arquivo, file);
			}
		}

		@Override
		public void novoArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoArquivo(InstrucaoSplit.this, arquivo.getFile());
				adicionar(arquivoTree, arquivo, file);
			}
		}

		private boolean valido(Arquivo arquivo) {
			return arquivo != null && arquivo.isDirectory();
		}

		private void adicionar(ArquivoTree arquivoTree, Arquivo arquivo, File file) {
			if (file != null) {
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

class TextArea extends TextPane implements MetaDialogoListener {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Rectangle rectangle;
	private static boolean paintER;

	TextArea() {
		addFocusListener(focusListenerInner);
		addCaretListener(this::processar);
		addKeyListener(keyListenerInner);
		rectangle = new Rectangle();
	}

	public static boolean isPaintER() {
		return paintER;
	}

	public static void setPaintER(boolean paintER) {
		TextArea.paintER = paintER;
	}

	public static void inverterPaintER() {
		setPaintER(!TextArea.paintER);
		InstrucaoPreferencia.setPaintER(paintER);
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
			if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_SPACE) {
				processar();
			}
		}

		private void processar() {
			String string = getSelectedText();
			if (Util.isEmpty(string)) {
				return;
			}
			string = Util.trim(string, '.', false);
			string = Util.trim(string, '.', true);
			try {
				InstrucaoMetadados.abrir(TextArea.this, string, TextArea.this);
			} catch (InstrucaoException ex) {
				LOG.warning(ex.getMessage());
			}
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

	private void processar(CaretEvent e) {
		TextUI textUI = getUI();
		try {
			Rectangle r = textUI.modelToView(this, e.getDot());
			if (r != null) {
				rectangle.width = getWidth();
				rectangle.height = r.height;
				rectangle.y = r.y;
				repaint();
			}
		} catch (BadLocationException ex) {
			LOG.warning(ex.getMessage());
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (paintER) {
			TextUI textUI = getUI();
			String text = getText();
			paintE(g, textUI, text);
			paintR(g, textUI, text);
			paintT(g, textUI, text);
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f));
		g.setColor(InstrucaoConstantes.COLOR_SEL);
		g.fillRect(0, rectangle.y, rectangle.width, rectangle.height);
	}

	private void paintE(Graphics g, TextUI textUI, String text) {
		int pos = text.indexOf(' ');
		while (pos != -1) {
			try {
				Rectangle r = textUI.modelToView(this, pos);
				if (r != null) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(r.x + 1, r.y + r.height / 3, 3, 3);
				}
				pos = text.indexOf(' ', pos + 1);
			} catch (BadLocationException e) {
				break;
			}
		}
	}

	private void paintR(Graphics g, TextUI textUI, String text) {
		int pos = text.indexOf('\n');
		while (pos != -1) {
			try {
				Rectangle r = textUI.modelToView(this, pos);
				if (r != null) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillRect(r.x, r.y, 5, 5);
					g.drawLine(r.x + 2, r.y, r.x + 2, r.y + r.height);
					g.drawLine(r.x + 4, r.y, r.x + 4, r.y + r.height);
				}
				pos = text.indexOf('\n', pos + 1);
			} catch (BadLocationException e) {
				break;
			}
		}
	}

	private void paintT(Graphics g, TextUI textUI, String text) {
		int pos = text.indexOf('\t');
		while (pos != -1) {
			try {
				Rectangle r = textUI.modelToView(this, pos);
				if (r != null) {
					g.setColor(Color.LIGHT_GRAY);
					desenhar(g, r, 0);
					desenhar(g, r, 2);
				}
				pos = text.indexOf('\t', pos + 1);
			} catch (BadLocationException e) {
				break;
			}
		}
	}

	private void desenhar(Graphics g, Rectangle r, int offset) {
		r.x += offset;
		g.drawLine(r.x, r.y, r.x + 3, r.y + r.height / 2);
		g.drawLine(r.x + 3, r.y + r.height / 2, r.x, r.y + r.height);
	}
}

class Aba extends Transferivel {
	private final PainelResultado painelResultado = new PainelResultado();
	private final TextArea textArea = new TextArea();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
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
		return getStringRelativo(new File(InstrucaoConstantes.INSTRUCAO), arquivo.getFile());
	}

	@Override
	public File getFile() {
		return arquivo.getFile();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanel(), criarPanelResultado());
		SwingUtilities.invokeLater(() -> split.setDividerLocation(.99));
		add(BorderLayout.CENTER, split);
	}

	private Panel criarPanel() {
		Panel panel = new Panel();
		Panel panelArea = new Panel();
		panelArea.add(BorderLayout.CENTER, textArea);
		scrollPane = new ScrollPane(panelArea);
		panel.add(BorderLayout.CENTER, scrollPane);
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
		private static final long serialVersionUID = 1L;
		private JTextPane textPane = new JTextPane();

		private PainelResultado() {
			add(BorderLayout.NORTH, new ToolbarPesquisa(textPane));
			add(BorderLayout.CENTER, new ScrollPane(textPane));
		}

		private void setText(String string) {
			textPane.setText(string);
			SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));
		}
	}

	private void abrir() {
		textArea.limpar();
		if (arquivo.getFile().exists()) {
			try {
				int value = getValueScrollPane();
				textArea.setText(conteudo(arquivo.getFile()));
				setValueScrollPane(value);
				InstrucaoCor.clearAttr(textArea.getStyledDocument());
				SwingUtilities.invokeLater(Aba.this::aplicarFontePreferencia);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, Aba.this);
			}
		}
	}

	private void aplicarFontePreferencia() {
		TextArea.setPaintER(InstrucaoPreferencia.getPaintER());
		Font font = InstrucaoPreferencia.getFontPreferencia();
		if (font != null) {
			toolbar.selecionarFont(font);
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

	private class Toolbar extends BarraButton implements ActionListener {
		private JComboBox<String> comboFontes = new JComboBox<>(InstrucaoConstantes.FONTES);
		private Action executarAcao = acaoIcon("label.executar", Icones.EXECUTAR);
		private Action compiladoAcao = acaoIcon("label.compilado", Icones.ABRIR);
		private Action paintERAcao = acaoIcon("label.paint_er", Icones.VERTICAL);
		private final TextField txtPesquisa = new TextField(35);
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini() {
			super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR, SALVAR, ATUALIZAR);
			atualizarAcao.text(InstrucaoMensagens.getString("label.compilar_arquivo"));
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			comboFontes.addItemListener(Toolbar.this::alterarFonte);
			compiladoAcao.setActionListener(e -> verCompilado());
			executarAcao.setActionListener(e -> executar());
			paintERAcao.setActionListener(e -> paintER());
			txtPesquisa.addActionListener(this);
			addButton(compiladoAcao);
			addButton(executarAcao);
			addButton(paintERAcao);
			add(txtPesquisa);
			add(label);
			add(comboFontes);
		}

		public void ini(String arqAbsoluto) {
			label.setText(arqAbsoluto);
			add(label);
		}

		Action acaoIcon(String chave, Icon icon) {
			return Action.acaoIcon(InstrucaoMensagens.getString(chave), icon);
		}

		private void paintER() {
			TextArea.inverterPaintER();
			textArea.repaint();
		}

		private void verCompilado() {
			try {
				File file = Compilador.getCompilado(arquivo.getFile());
				if (!file.exists()) {
					throw new IOException("Arquivo inexistente! " + file);
				}
				Util.conteudo(Aba.this, file);
			} catch (IOException e) {
				Util.mensagem(Aba.this, e.getMessage());
			}
		}

		private void executar() {
			String biblioteca = arquivo.getFile().getName();
			try {
				Processador processador = new Processador();
				List<Object> resposta = processador.processar(biblioteca, "main");
				painelResultado.setText(resposta.toString());
			} catch (InstrucaoException ex) {
				painelResultado.setText(Util.getStackTrace(InstrucaoConstantes.PAINEL_INSTRUCAO, ex));
			}
		}

		@Override
		protected void atualizar() {
			try {
				Compilador compilador = new Compilador();
				BibliotecaContexto biblio = compilador.compilar(arquivo.getFile());
				boolean resp = biblio != null;
				painelResultado.setText(resp ? InstrucaoMensagens.getString("msg.compilado")
						: InstrucaoMensagens.getString("msg.nao_compilado"));
				if (resp) {
					List<Token> tokens = new ArrayList<>(compilador.getTokens());
					biblio.filtroConstParam(tokens);
					InstrucaoCor.processar(textArea.getStyledDocument(), tokens);
				}
			} catch (IOException | InstrucaoException ex) {
				painelResultado.setText(Util.getStackTrace(InstrucaoConstantes.PAINEL_INSTRUCAO, ex));
			}
		}

		private void alterarFonte(ItemEvent e) {
			if (ItemEvent.SELECTED == e.getStateChange()) {
				Object object = comboFontes.getSelectedItem();
				if (object instanceof String) {
					Font font = getFont();
					alterar(font, (String) object);
				}
			}
		}

		private void alterar(Font font, String nome) {
			if (font != null) {
				Font nova = new Font(nome, font.getStyle(), font.getSize());
				selecionarFont(nova);
			}
		}

		private void selecionarFont(Font font) {
			InstrucaoPreferencia.setFontPreferencia(font);
			comboFontes.setSelectedItem(font.getName());
			textArea.setFont(font);
		}

		@Override
		protected void baixar() {
			abrir();
			selecao = null;
			label.limpar();
		}

		@Override
		protected void limpar() {
			textArea.limpar();
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
		protected void salvar() {
			if (Util.confirmaSalvar(Aba.this, Constantes.TRES)) {
				salvarArquivo(arquivo.getFile());
			}
		}

		private void salvarArquivo(File file) {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, Aba.this);
			}
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
	}
}

class PanelRoot extends Panel {
	private static final long serialVersionUID = 1L;

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

	void setRoot(Component c) throws InstrucaoException {
		if (getComponentCount() > 0) {
			throw new InstrucaoException("getComponentCount() > 0", false);
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

	void excluir(Arquivo arquivo) throws InstrucaoException, SeparadorException {
		Transferivel objeto = getTransferivel(arquivo.getFile());
		while (objeto != null) {
			Fichario fichario = getFichario(objeto);
			if (fichario != null) {
				if (!fichario.contem(objeto)) {
					throw new InstrucaoException("!fichario.contem(objeto)", false);
				} else {
					fichario.excluir(objeto);
				}
			} else {
				throw new InstrucaoException("fichario == null", false);
			}
			objeto = getTransferivel(arquivo.getFile());
		}
	}
}

class AnotacaoHandler extends XMLHandler {
	private final ArquivoModelo modelo;
	private final PanelRoot root;
	Separador separador;
	Fichario fichario;

	AnotacaoHandler(PanelRoot root, ArquivoModelo modelo) {
		this.modelo = modelo;
		this.root = root;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("fichario".equals(qName)) {
			fichario = new Fichario();
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
			File fileRoot = new File(InstrucaoConstantes.INSTRUCAO);
			File file = new File(fileRoot, nome);
			Arquivo arquivo = modelo.getArquivo(file);
			if (arquivo != null) {
				InstrucaoSplit.novaAba(fichario, arquivo);
			} else {
				InstrucaoSplit.novaAba(fichario, file);
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

class InstrucaoCor {
	private static final MutableAttributeSet MAGENTA = new SimpleAttributeSet();
	private static final MutableAttributeSet GREEN2 = new SimpleAttributeSet();
	private static final MutableAttributeSet BLUE2 = new SimpleAttributeSet();
	private static final MutableAttributeSet GRAY = new SimpleAttributeSet();
	public static final MutableAttributeSet PLAIN = new SimpleAttributeSet();
	private static final MutableAttributeSet BLUE = new SimpleAttributeSet();
	private static final MutableAttributeSet BOLD = new SimpleAttributeSet();
	private static final MutableAttributeSet RED2 = new SimpleAttributeSet();
	private static final MutableAttributeSet RED = new SimpleAttributeSet();
	private static final MutableAttributeSet TAG = new SimpleAttributeSet();

	private InstrucaoCor() {
	}

	static void processar(StyledDocument doc, List<Token> tokens) {
		for (Token token : tokens) {
			if (token.isReservado()) {
				set(doc, token, RED);
			} else if (token.isConstante()) {
				set(doc, token, BLUE2);
			} else if (token.isParametro()) {
				set(doc, token, GREEN2);
			} else if (token.isFuncao()) {
				set(doc, token, MAGENTA);
			} else if (token.isString()) {
				set2(doc, token, BLUE);
			} else if (token.isComentario()) {
				set2(doc, token, GRAY);
			} else if (token.isEspecial()) {
				set(doc, token, BOLD);
			} else if (token.isNumero()) {
				set(doc, token, RED2);
			}
		}
	}

	static void set(StyledDocument doc, Token token, MutableAttributeSet att) {
		doc.setCharacterAttributes(token.getIndice(), token.getString().length(), att, true);
	}

	static void set2(StyledDocument doc, Token token, MutableAttributeSet att) {
		doc.setCharacterAttributes(token.getIndice(), token.getIndice2() - token.getIndice(), att, true);
	}

	static void clearAttr(StyledDocument doc) {
		doc.setCharacterAttributes(0, doc.getLength(), PLAIN, true);
	}

	static {
		StyleConstants.setForeground(GRAY, new Color(192, 192, 192));
		StyleConstants.setBackground(TAG, new Color(225, 225, 225));
		StyleConstants.setForeground(GREEN2, new Color(0, 125, 0));
		StyleConstants.setForeground(BLUE2, new Color(0, 0, 125));
		StyleConstants.setForeground(RED, new Color(100, 25, 51));
		StyleConstants.setForeground(BLUE, new Color(0, 0, 255));
		StyleConstants.setForeground(RED2, new Color(255, 0, 0));
		StyleConstants.setForeground(MAGENTA, Color.MAGENTA);
		StyleConstants.setBold(MAGENTA, true);
		StyleConstants.setBold(GREEN2, true);
		StyleConstants.setBold(BLUE2, true);
		StyleConstants.setBold(GRAY, true);
		StyleConstants.setBold(BLUE, true);
		StyleConstants.setBold(BOLD, true);
		StyleConstants.setBold(RED2, true);
		StyleConstants.setBold(RED, true);
	}
}