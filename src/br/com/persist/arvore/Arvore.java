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

import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;
import br.com.persist.listener.ArvoreListener;
import br.com.persist.renderer.TreeCellRenderer;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;

public class Arvore extends JTree {
	private static final long serialVersionUID = 1L;
	private final transient List<ArvoreListener> ouvintes;
	private ArvorePopup arvorePopup = new ArvorePopup();
	private boolean popupDesabilitado;

	public Arvore(TreeModel newModel) {
		super(newModel);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setBorder(BorderFactory.createEmptyBorder());
		setCellRenderer(new TreeCellRenderer());
		addMouseListener(mouseListener_);
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

	private MouseListener mouseListener_ = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				ouvintes.forEach(o -> o.abrirFichArquivo(Arvore.this));
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
				arvorePopup.show(Arvore.this, e.getX(), e.getY());
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
		private Action abrirFormAcao = Action.actionMenu("label.abrir_formulario", Icones.ABRIR);
		private Action abrirFichAcao = Action.actionMenu("label.abrir_fichario", Icones.ABRIR);
		private Action atualizarAcao = Action.actionMenu("label.atualizar", Icones.ATUALIZAR);
		private Action excluirAcao = Action.actionMenu("label.excluir", Icones.EXCLUIR);

		public ArvorePopup() {
			add(new MenuItem(atualizarAcao));
			addSeparator();
			add(new MenuItem(abrirFormAcao));
			add(new MenuItem(abrirFichAcao));

			abrirFormAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirFormArquivo(Arvore.this)));
			abrirFichAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirFichArquivo(Arvore.this)));
			atualizarAcao.setActionListener(e -> ouvintes.forEach(o -> o.atualizarArvore(Arvore.this)));
			excluirAcao.setActionListener(e -> ouvintes.forEach(o -> o.excluirArquivo(Arvore.this)));
		}
	}
}