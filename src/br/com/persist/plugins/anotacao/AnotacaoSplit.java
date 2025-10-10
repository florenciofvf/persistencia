package br.com.persist.plugins.anotacao;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.arquivo.ArquivoModelo;
import br.com.persist.arquivo.ArquivoTree;
import br.com.persist.arquivo.ArquivoTreeListener;
import br.com.persist.arquivo.ArquivoTreeUtil;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SplitPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLHandler;
import br.com.persist.marca.XMLUtil;
import br.com.persist.painel.Fichario;
import br.com.persist.painel.Root;
import br.com.persist.painel.Separador;
import br.com.persist.painel.SeparadorException;
import br.com.persist.painel.Transferivel;

class AnotacaoSplit extends SplitPane {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	AnotacaoContainer container;
	private final File fileRoot;
	private ArquivoTree tree;
	private PanelRoot panel;

	AnotacaoSplit() {
		super(HORIZONTAL_SPLIT);
		fileRoot = new File(AnotacaoConstantes.ANOTACOES);
	}

	void inicializar(AnotacaoContainer container) {
		this.container = container;
		File file = new File(fileRoot, AnotacaoConstantes.IGNORADOS);
		List<String> ignorados = ArquivoUtil.getIgnorados(file);
		ArquivoUtil.arquivoIgnorado(ignorados, AnotacaoPreferencia.isExibirArqIgnorados());
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
		util.abrirTag2("anotacoes");
		panel.salvar(util);
		util.finalizarTag("anotacoes");
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

	void abrir(Arquivo arquivo) throws AnotacaoException {
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
		fichario.setTabPlacement(AnotacaoPreferencia.getAnotacaoPosicaoAbaFichario());
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
				String nome = ArquivoUtil.getNome(AnotacaoSplit.this, arquivo.getName());
				if (nome != null && arquivo.renomear(nome)) {
					ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
					panel.renomear();
				}
			}
		}

		@Override
		public void excluirArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null && Util.confirmar(AnotacaoSplit.this, "msg.confirma_exclusao")) {
				arquivo.excluir();
				ArquivoTreeUtil.excluirEstrutura(arquivoTree, arquivo);
				try {
					panel.excluir(arquivo);
				} catch (AnotacaoException | SeparadorException ex) {
					Util.mensagem(AnotacaoSplit.this, ex.getMessage());
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
				String resp = Util.clonar(AnotacaoSplit.this, arquivo.getFile(), ref);
				if (Preferencias.isExibirTotalBytesClonados()) {
					Util.mensagem(AnotacaoSplit.this, resp);
				}
				adicionar(arquivoTree, arquivo.getPai(), ref.get());
			} catch (IOException e) {
				Util.mensagem(AnotacaoSplit.this, e.getMessage());
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
				Util.mensagem(AnotacaoSplit.this, e.getMessage());
			}
		}

		@Override
		public void abrirArquivo(ArquivoTree arquivoTree) {
			try {
				abrir(arquivoTree.getObjetoSelecionado());
			} catch (AnotacaoException ex) {
				Util.mensagem(AnotacaoSplit.this, ex.getMessage());
			}
		}

		@Override
		public void novoDiretorio(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoDiretorio(AnotacaoSplit.this, arquivo.getFile());
				adicionar(arquivoTree, arquivo, file);
			}
		}

		@Override
		public void novoArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoArquivo(AnotacaoSplit.this, arquivo.getFile());
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
	private static final long serialVersionUID = 1L;

	Editor() {
		addFocusListener(focusListenerInner);
	}

	private transient FocusListener focusListenerInner = new FocusAdapter() {
		@Override
		public void focusGained(java.awt.event.FocusEvent e) {
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
}

class Aba extends Transferivel {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final Editor editor = new Editor();
	final transient Arquivo arquivo;

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
		return getStringRelativo(new File(AnotacaoConstantes.ANOTACOES), arquivo.getFile());
	}

	@Override
	public File getFile() {
		return arquivo.getFile();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		JScrollPane scrollPane = new JScrollPane(editor);
		scrollPane.setRowHeaderView(new TextEditorLine(editor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		editor.setListener(e -> toolbar.focusInputPesquisar());
	}

	private void abrir() {
		editor.limpar();
		if (arquivo.getFile().exists()) {
			try {
				editor.setText(ArquivoUtil.getString(arquivo.getFile()));
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
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini() {
			super.ini(new Nil(), BAIXAR, LIMPAR, SALVAR, COPIAR, COLAR);
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		public void ini(String arqAbsoluto) {
			label.setText(arqAbsoluto);
			add(label);
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

	void setRoot(Component c) throws AnotacaoException {
		if (getComponentCount() > 0) {
			throw new AnotacaoException("getComponentCount() > 0", false);
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

	void excluir(Arquivo arquivo) throws AnotacaoException, SeparadorException {
		Transferivel objeto = getTransferivel(arquivo.getFile());
		while (objeto != null) {
			Fichario fichario = getFichario(objeto);
			if (fichario != null) {
				if (!fichario.contem(objeto)) {
					throw new AnotacaoException("!fichario.contem(objeto)", false);
				} else {
					fichario.excluir(objeto);
				}
			} else {
				throw new AnotacaoException("fichario == null", false);
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
			fichario.setTabPlacement(AnotacaoPreferencia.getAnotacaoPosicaoAbaFichario());
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
			File fileRoot = new File(AnotacaoConstantes.ANOTACOES);
			File file = new File(fileRoot, nome);
			Arquivo arquivo = modelo.getArquivo(file);
			if (arquivo != null) {
				AnotacaoSplit.novaAba(fichario, arquivo);
			} else {
				AnotacaoSplit.novaAba(fichario, file);
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