package br.com.persist.arvore;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import br.com.persist.Arquivo;
import br.com.persist.comp.Popup;
import br.com.persist.listener.ArvoreListener;
import br.com.persist.renderer.ArquivoTreeCellRenderer;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.MenuPadrao1;

public class Arvore extends JTree {
	private static final long serialVersionUID = 1L;
	private final transient List<ArvoreListener> ouvintes;
	private ArvorePopup arvorePopup = new ArvorePopup();
	private boolean popupDesabilitado;

	public Arvore(TreeModel newModel) {
		super(newModel);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setCellRenderer(new ArquivoTreeCellRenderer());
		setBorder(BorderFactory.createEmptyBorder());
		addMouseListener(mouseListenerInner);
		ouvintes = new ArrayList<>();
		setShowsRootHandles(true);
		setRootVisible(true);
	}

	public void adicionarOuvinte(ArvoreListener listener) {
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

		ArvoreUtil.selecionarObjeto(this, arquivo);
	}

	public void excluirSelecionado() {
		Arquivo selecionado = getObjetoSelecionado();

		if (selecionado == null) {
			return;
		}

		ArvoreUtil.excluirEstrutura(this, selecionado);
	}

	private transient MouseListener mouseListenerInner = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				ouvintes.forEach(o -> o.abrirFichArquivo(Arvore.this));
			} else {
				ouvintes.forEach(o -> o.clickArquivo(Arvore.this));
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			if (!e.isPopupTrigger() || popupDesabilitado || getObjetoSelecionado() == null) {
				return;
			}

			TreePath arvoreCli = getClosestPathForLocation(e.getX(), e.getY());
			TreePath arvoreSel = getSelectionPath();

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
					arvorePopup.show(Arvore.this, e.getX(), e.getY());
				} else {
					setSelectionPath(null);
				}
			} else {
				setSelectionPath(null);
			}
		}
	};

	public void desabilitarPopup() {
		popupDesabilitado = true;
	}

	private class ArvorePopup extends Popup {
		private static final long serialVersionUID = 1L;
		private Action selecionarAcao = Action.actionMenu("label.selecionar", Icones.CURSOR);
		private Action atualizarAcao = Action.actionMenu("label.status", Icones.ATUALIZAR);
		private Action fecharAcao = Action.actionMenu("label.fechar", Icones.FECHAR);
		private MenuAbrir menuAbrir = new MenuAbrir();

		public ArvorePopup() {
			add(menuAbrir);
			addMenuItem(true, selecionarAcao);
			addMenuItem(true, fecharAcao);
			addMenuItem(true, atualizarAcao);

			selecionarAcao.setActionListener(e -> ouvintes.forEach(o -> o.selecionarArquivo(Arvore.this)));
			atualizarAcao.setActionListener(e -> ouvintes.forEach(o -> o.atualizarArvore(Arvore.this)));
			fecharAcao.setActionListener(e -> ouvintes.forEach(o -> o.fecharArquivo(Arvore.this)));
		}

		private void preShow(Arquivo arquivo) {
			boolean ehArquivo = arquivo.isFile();
			selecionarAcao.setEnabled(ehArquivo);
			atualizarAcao.setEnabled(ehArquivo);
			fecharAcao.setEnabled(ehArquivo);
			menuAbrir.setEnabled(ehArquivo);
		}

		class MenuAbrir extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrir() {
				super("label.abrir", Icones.ABRIR, false);

				formularioAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirFormArquivo(Arvore.this)));
				ficharioAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirFichArquivo(Arvore.this)));
			}
		}
	}
}