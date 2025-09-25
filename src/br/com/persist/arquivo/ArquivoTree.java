package br.com.persist.arquivo;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreePath;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.Popup;
import br.com.persist.componente.Tree;

public class ArquivoTree extends Tree {
	private final transient List<ArquivoTreeListener> ouvintes;
	private ArquivoPopup arquivoPopup = new ArquivoPopup();
	private static final long serialVersionUID = 1L;

	public ArquivoTree(ArquivoModelo modelo) {
		super(modelo);
		addMouseListener(mouseListenerInner);
		addKeyListener(keyListenerInner);
		ouvintes = new ArrayList<>();
	}

	public ArquivoPesquisa getPesquisa(ArquivoPesquisa pesquisa, String string, boolean porParte) {
		if (pesquisa == null) {
			return new ArquivoPesquisa(this, string, porParte);
		} else if (pesquisa.igual(string, porParte)) {
			return pesquisa;
		}
		return new ArquivoPesquisa(this, string, porParte);
	}

	public ArquivoPopup getArquivoPopup() {
		return arquivoPopup;
	}

	public void adicionarOuvinte(ArquivoTreeListener listener) {
		if (listener != null) {
			ouvintes.add(listener);
		}
	}

	public ArquivoModelo getModelo() {
		return (ArquivoModelo) getModel();
	}

	public Arquivo getRaiz() {
		return (Arquivo) getModelo().getRoot();
	}

	public Arquivo getObjetoSelecionado() {
		TreePath path = getSelectionPath();
		if (path == null) {
			return null;
		}
		if (path.getLastPathComponent() instanceof Arquivo) {
			return (Arquivo) path.getLastPathComponent();
		}
		return null;
	}

	public void selecionarArquivo(Arquivo arquivo) {
		if (arquivo != null) {
			ArquivoTreeUtil.selecionarObjeto(this, arquivo);
		}
	}

	public void excluirSelecionado() {
		Arquivo selecionado = getObjetoSelecionado();
		if (selecionado != null) {
			ArquivoTreeUtil.excluirEstrutura(this, selecionado);
		}
	}

	public void selecionar(String nome, boolean porParte) {
		Arquivo raiz = getRaiz();
		Arquivo arquivo = raiz.getArquivo(nome, porParte);
		if (arquivo != null) {
			ArquivoTreeUtil.selecionarObjeto(this, arquivo);
		} else {
			setSelectionPath(null);
		}
	}

	public void preencher(List<Arquivo> lista, String nome, boolean porParte) {
		Arquivo raiz = getRaiz();
		raiz.preencher(lista, nome, porParte);
	}

	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		Arquivo raiz = getRaiz();
		raiz.contemConteudo(set, string, porParte);
		if (set.isEmpty()) {
			Util.beep();
		}
	}

	private transient KeyAdapter keyListenerInner = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				mouseListenerInner
						.mouseClicked(new MouseEvent(ArquivoTree.this, 0, 0, 0, 0, 0, Constantes.DOIS, false));
			}
		}
	};

	private transient MouseListener mouseListenerInner = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			popupTrigger = false;
			checkPopupTrigger(e);
			if (!e.isPopupTrigger() || getObjetoSelecionado() == null) {
				return;
			}
			TreePath clicado = getClosestPathForLocation(e.getX(), e.getY());
			TreePath selecionado = getSelectionPath();
			popupTrigger = true;
			if (!validos(clicado, selecionado)) {
				setSelectionPath(null);
				return;
			}
			if (!localValido(clicado, e)) {
				setSelectionPath(null);
				return;
			}
			if (clicado.equals(selecionado)) {
				if (selecionado.getLastPathComponent() instanceof Arquivo) {
					Arquivo arquivo = (Arquivo) selecionado.getLastPathComponent();
					arquivoPopup.preShow(arquivo);
					arquivoPopup.show(ArquivoTree.this, e.getX(), e.getY());
				} else {
					setSelectionPath(null);
				}
			} else {
				setSelectionPath(null);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (popupTrigger) {
				return;
			}
			TreePath clicado = getClosestPathForLocation(e.getX(), e.getY());
			TreePath selecionado = getSelectionPath();
			if (!validos(clicado, selecionado)) {
				setSelectionPath(null);
				return;
			}
			if (!localValido(clicado, e)) {
				setSelectionPath(null);
				return;
			}
			if (e.getClickCount() >= Constantes.DOIS) {
				Arquivo arquivo = getObjetoSelecionado();
				if (arquivo == null) {
					return;
				}
				if (arquivo.isFile()) {
					ouvintes.forEach(o -> o.abrirArquivo(ArquivoTree.this));
				} else if (arquivo.isDirectory()) {
					ouvintes.forEach(o -> o.diretorioArquivo(ArquivoTree.this));
				}
			}
		}
	};

	public class ArquivoPopup extends Popup {
		private Action novoDiretorioAcao = actionMenu("label.novo_diretorio", Icones.ABRIR);
		private Action novoArquivoAcao = actionMenu("label.novo_arquivo", Icones.PANEL4);
		private Action diretorioAcao = actionMenu("label.diretorio", Icones.ABRIR);
		private Action renomearAcao = actionMenu("label.renomear", Icones.RULE);
		private Action abrirAcao = actionMenu("label.abrir", Icones.ABRIR);
		private Action excluirAcao = Action.actionMenuExcluir();
		private Action clonarAcao = Action.actionMenuClonar();
		private static final long serialVersionUID = 1L;

		private ArquivoPopup() {
			addMenuItem(novoDiretorioAcao);
			addMenuItem(novoArquivoAcao);
			addMenuItem(true, diretorioAcao);
			addMenuItem(renomearAcao);
			addMenuItem(excluirAcao);
			addMenuItem(abrirAcao);
			addSeparator();
			addMenuItem(clonarAcao);
			novoDiretorioAcao.setActionListener(e -> ouvintes.forEach(o -> o.novoDiretorio(ArquivoTree.this)));
			diretorioAcao.setActionListener(e -> ouvintes.forEach(o -> o.diretorioArquivo(ArquivoTree.this)));
			renomearAcao.setActionListener(e -> ouvintes.forEach(o -> o.renomearArquivo(ArquivoTree.this)));
			novoArquivoAcao.setActionListener(e -> ouvintes.forEach(o -> o.novoArquivo(ArquivoTree.this)));
			excluirAcao.setActionListener(e -> ouvintes.forEach(o -> o.excluirArquivo(ArquivoTree.this)));
			clonarAcao.setActionListener(e -> ouvintes.forEach(o -> o.clonarArquivo(ArquivoTree.this)));
			abrirAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirArquivo(ArquivoTree.this)));
		}

		private void preShow(Arquivo arquivo) {
			if (getRaiz() == arquivo) {
				diretorioAcao.setEnabled(arquivo.pathValido());
				novoDiretorioAcao.setEnabled(true);
				novoArquivoAcao.setEnabled(true);
				renomearAcao.setEnabled(false);
				excluirAcao.setEnabled(false);
				clonarAcao.setEnabled(false);
				abrirAcao.setEnabled(false);
				return;
			}
			boolean dir = arquivo.isDirectory();
			boolean file = arquivo.isFile();
			boolean both = dir || file;
			diretorioAcao.setEnabled(both && arquivo.pathValido());
			novoDiretorioAcao.setEnabled(dir);
			novoArquivoAcao.setEnabled(dir);
			renomearAcao.setEnabled(both);
			excluirAcao.setEnabled(both);
			clonarAcao.setEnabled(file);
			abrirAcao.setEnabled(file);
		}
	}
}