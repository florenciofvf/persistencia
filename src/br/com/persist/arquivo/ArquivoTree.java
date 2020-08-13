package br.com.persist.arquivo;

import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import br.com.persist.componente.Popup;
import br.com.persist.componente.Tree;
import br.com.persist.icone.Icones;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.MenuPadrao1;

public class ArquivoTree extends Tree {
	private static final long serialVersionUID = 1L;
	private final transient List<ArquivoTreeListener> ouvintes;
	private ArquivoPopup arvorePopup = new ArquivoPopup();

	public ArquivoTree(TreeModel newModel) {
		super(newModel);
		setCellRenderer(new ArquivoTreeCellRenderer());
		addMouseListener(mouseListenerInner);
		addKeyListener(keyListenerInner);
		ouvintes = new ArrayList<>();
	}

	public void adicionarOuvinte(ArquivoTreeListener listener) {
		if (listener == null) {
			return;
		}

		ouvintes.add(listener);
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
		if (arquivo == null) {
			return;
		}

		ArquivoTreeUtil.selecionarObjeto(this, arquivo);
	}

	public void excluirSelecionado() {
		Arquivo selecionado = getObjetoSelecionado();

		if (selecionado == null) {
			return;
		}

		ArquivoTreeUtil.excluirEstrutura(this, selecionado);
	}

	private transient KeyAdapter keyListenerInner = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				popupTrigger = false;
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

			if (!e.isPopupTrigger() || popupDesabilitado || getObjetoSelecionado() == null) {
				return;
			}

			TreePath arvoreCli = getClosestPathForLocation(e.getX(), e.getY());
			TreePath arvoreSel = getSelectionPath();
			popupTrigger = true;

			if (arvoreCli == null || arvoreSel == null) {
				setSelectionPath(null);
				return;
			}

			Rectangle rect = getPathBounds(arvoreCli);

			if (rect == null || !rect.contains(e.getX(), e.getY())) {
				setSelectionPath(null);
				return;
			}

			if (arvoreCli.equals(arvoreSel)) {
				if (arvoreSel.getLastPathComponent() instanceof Arquivo) {
					Arquivo arquivo = (Arquivo) arvoreSel.getLastPathComponent();
					arvorePopup.preShow(arquivo);
					arvorePopup.show(ArquivoTree.this, e.getX(), e.getY());
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

			if (e.getClickCount() >= Constantes.DOIS) {
				ouvintes.forEach(o -> o.abrirFichArquivo(ArquivoTree.this));
			} else {
				TreePath arvoreCli = getClosestPathForLocation(e.getX(), e.getY());

				if (arvoreCli != null) {
					Arquivo arquivo = (Arquivo) arvoreCli.getLastPathComponent();

					if (arquivo == null || !arquivo.isFile()) {
						return;
					}

					ouvintes.forEach(o -> o.clickArquivo(ArquivoTree.this));
				}
			}
		}
	};

	public void desabilitarPopup() {
		popupDesabilitado = true;
	}

	private class ArquivoPopup extends Popup {
		private static final long serialVersionUID = 1L;
		private Action fecharAcao = Action.actionMenu(Constantes.LABEL_FECHAR, Icones.FECHAR);
		private Action selecionarAcao = Action.actionMenu("label.selecionar", Icones.CURSOR);
		private Action atualizarAcao = Action.actionMenu("label.status", Icones.ATUALIZAR);
		private Action excluirAcao = Action.actionMenu("label.excluir2", Icones.EXCLUIR);
		private MenuAbrir menuAbrir = new MenuAbrir();

		private ArquivoPopup() {
			add(menuAbrir);
			addMenuItem(true, selecionarAcao);
			addMenuItem(true, fecharAcao);
			addMenuItem(true, excluirAcao);
			addMenuItem(true, atualizarAcao);

			selecionarAcao.setActionListener(e -> ouvintes.forEach(o -> o.selecionarArquivo(ArquivoTree.this)));
			atualizarAcao.setActionListener(e -> ouvintes.forEach(o -> o.atualizarArvore(ArquivoTree.this)));
			excluirAcao.setActionListener(e -> ouvintes.forEach(o -> o.excluirArquivo(ArquivoTree.this)));
			fecharAcao.setActionListener(e -> ouvintes.forEach(o -> o.fecharArquivo(ArquivoTree.this)));
		}

		private void preShow(Arquivo arquivo) {
			boolean ehArquivo = arquivo.isFile();
			excluirAcao.setEnabled(ehArquivo && arquivo.getPai() != null);
			selecionarAcao.setEnabled(ehArquivo);
			atualizarAcao.setEnabled(ehArquivo);
			fecharAcao.setEnabled(ehArquivo);
			menuAbrir.setEnabled(ehArquivo);
		}

		private class MenuAbrir extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;
			private Action pastaAcao = Action.actionMenu("label.diretorio", Icones.ABRIR);

			private MenuAbrir() {
				super("label.abrir", Icones.ABRIR, false);
				addSeparator();
				addMenuItem(pastaAcao);

				formularioAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirFormArquivo(ArquivoTree.this)));
				ficharioAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirFichArquivo(ArquivoTree.this)));
				pastaAcao.setActionListener(e -> ouvintes.forEach(o -> o.pastaArquivo(ArquivoTree.this)));
			}
		}
	}
}