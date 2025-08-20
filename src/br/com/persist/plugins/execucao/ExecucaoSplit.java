package br.com.persist.plugins.execucao;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

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
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SplitPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLHandler;
import br.com.persist.marca.XMLUtil;
import br.com.persist.painel.Fichario;
import br.com.persist.painel.Root;
import br.com.persist.painel.Separador;
import br.com.persist.painel.SeparadorException;
import br.com.persist.painel.Transferivel;

class ExecucaoSplit extends SplitPane {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Formulario formulario;
	private final File fileRoot;
	private ArquivoTree tree;
	private PanelRoot panel;

	ExecucaoSplit(Formulario formulario) {
		super(HORIZONTAL_SPLIT);
		this.formulario = formulario;
		fileRoot = new File(ExecucaoConstantes.EXECUCOES);
	}

	void inicializar() {
		File file = new File(fileRoot, ExecucaoConstantes.IGNORADOS);
		List<String> ignorados = ArquivoUtil.getIgnorados(file);
		ArquivoUtil.arquivoIgnorado(ignorados, ExecucaoPreferencia.isExibirArqIgnorados());
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
		util.abrirTag2("execucoes");
		panel.salvar(util);
		util.finalizarTag("execucoes");
		util.close();
	}

	void abrir() {
		File file = new File(fileRoot, "hierarquia.xml");
		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new ExecucaoHandler(panel, tree.getModelo(), formulario));
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	void abrir(Arquivo arquivo, Formulario formulario) throws ExecucaoException {
		if (arquivo == null) {
			return;
		}
		Fichario fichario = panel.getFicharioSelecionado();
		if (fichario != null) {
			novaAba(fichario, arquivo, formulario);
		} else {
			fichario = panel.getFicharioPrimeiro();
			if (fichario != null) {
				novaAba(fichario, arquivo, formulario);
			} else {
				fichario = novoFichario(arquivo, formulario);
				panel.setRoot(fichario);
			}
		}
		SwingUtilities.updateComponentTreeUI(panel);
	}

	public void contemConteudo(Set<String> set, String string) {
		tree.contemConteudo(set, string);
	}

	private Fichario novoFichario(Arquivo arquivo, Formulario formulario) {
		Fichario fichario = new Fichario();
		fichario.setTabPlacement(ExecucaoPreferencia.getExecucaoPosicaoAbaFichario());
		novaAba(fichario, arquivo, formulario);
		return fichario;
	}

	public static void novaAba(Fichario fichario, Arquivo arquivo, Formulario formulario) {
		fichario.addTab(arquivo.getName(), new Aba(arquivo, formulario));
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
				String nome = ArquivoUtil.getNome(ExecucaoSplit.this, arquivo.getName());
				if (nome != null && arquivo.renomear(nome)) {
					ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
					panel.renomear();
				}
			}
		}

		@Override
		public void excluirArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null && Util.confirmar(ExecucaoSplit.this, "msg.confirma_exclusao")) {
				arquivo.excluir();
				ArquivoTreeUtil.excluirEstrutura(arquivoTree, arquivo);
				try {
					panel.excluir(arquivo);
				} catch (ExecucaoException | SeparadorException ex) {
					Util.mensagem(ExecucaoSplit.this, ex.getMessage());
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
				String resp = Util.clonar(ExecucaoSplit.this, arquivo.getFile());
				Util.mensagem(ExecucaoSplit.this, resp);
			} catch (IOException e) {
				Util.mensagem(ExecucaoSplit.this, e.getMessage());
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
				Util.mensagem(ExecucaoSplit.this, e.getMessage());
			}
		}

		@Override
		public void abrirArquivo(ArquivoTree arquivoTree) {
			try {
				abrir(arquivoTree.getObjetoSelecionado(), formulario);
			} catch (ExecucaoException ex) {
				Util.mensagem(ExecucaoSplit.this, ex.getMessage());
			}
		}

		@Override
		public void novoDiretorio(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoDiretorio(ExecucaoSplit.this, arquivo.getFile());
				adicionar(arquivoTree, arquivo, file);
			}
		}

		@Override
		public void novoArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoArquivo(ExecucaoSplit.this, arquivo.getFile());
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
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private static final long serialVersionUID = 1L;
	final transient Arquivo arquivo;
	private final AbaView abaView;
	private final AbaText abaText;

	Aba(Arquivo arquivo, Formulario formulario) {
		abaView = new AbaView(arquivo.getFile(), formulario);
		tabbedPane.addChangeListener(changeListenerInner);
		this.arquivo = Objects.requireNonNull(arquivo);
		abaText = new AbaText(arquivo);
		abaText.abrir();
		montarLayout();
		abaText.ini();
	}

	Aba(File file) {
		abaText = new AbaText(null);
		abaText.ini(Mensagens.getString("msg.arquivo_inexistente") + " " + file.getAbsolutePath());
		tabbedPane.addChangeListener(changeListenerInner);
		add(BorderLayout.NORTH, abaText.toolbar);
		this.arquivo = null;
		this.abaView = null;
	}

	private transient ChangeListener changeListenerInner = e -> {
		Component c = getParent();
		while (c != null) {
			if (c instanceof Fichario) {
				Fichario.setSelecionado((Fichario) c);
				break;
			}
			c = c.getParent();
		}
	};

	private void montarLayout() {
		tabbedPane.addTab("Text", abaText);
		tabbedPane.addTab("View", abaView);
		add(BorderLayout.CENTER, tabbedPane);
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
		return getStringRelativo(new File(ExecucaoConstantes.EXECUCOES), arquivo.getFile());
	}

	@Override
	public File getFile() {
		return arquivo.getFile();
	}

	@Override
	public void processar(Fichario fichario, int indice, Map<String, Object> map) {
		if (map.containsKey(Transferivel.RENOMEAR)) {
			fichario.setTitleAt(indice, arquivo.getName());
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

	void setRoot(Component c) throws ExecucaoException {
		if (getComponentCount() > 0) {
			throw new ExecucaoException("getComponentCount() > 0", false);
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

	void excluir(Arquivo arquivo) throws ExecucaoException, SeparadorException {
		Transferivel objeto = getTransferivel(arquivo.getFile());
		while (objeto != null) {
			Fichario fichario = getFichario(objeto);
			if (fichario != null) {
				if (!fichario.contem(objeto)) {
					throw new ExecucaoException("!fichario.contem(objeto)", false);
				} else {
					fichario.excluir(objeto);
				}
			} else {
				throw new ExecucaoException("fichario == null", false);
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

class ExecucaoHandler extends XMLHandler {
	private final Formulario formulario;
	private final ArquivoModelo modelo;
	private final PanelRoot root;
	Separador separador;
	Fichario fichario;

	ExecucaoHandler(PanelRoot root, ArquivoModelo modelo, Formulario formulario) {
		this.formulario = formulario;
		this.modelo = modelo;
		this.root = root;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("fichario".equals(qName)) {
			fichario = new Fichario();
			fichario.setTabPlacement(ExecucaoPreferencia.getExecucaoPosicaoAbaFichario());
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
			File fileRoot = new File(ExecucaoConstantes.EXECUCOES);
			File file = new File(fileRoot, nome);
			Arquivo arquivo = modelo.getArquivo(file);
			if (arquivo != null) {
				ExecucaoSplit.novaAba(fichario, arquivo, formulario);
			} else {
				ExecucaoSplit.novaAba(fichario, file);
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