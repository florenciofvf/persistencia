package br.com.persist.arvore;

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
import br.com.persist.util.Action;
import br.com.persist.util.Icones;

public class Arvore extends JTree {
	private static final long serialVersionUID = 1L;
	private ArvorePopup arvorePopup = new ArvorePopup();
	private final List<ArvoreListener> ouvintes;
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
			// ouvintes.forEach(o -> o.selecionadoObjeto(Arvore.this));
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
			if (!e.isPopupTrigger()) {
				return;
			}

			Arquivo selecionado = getObjetoSelecionado();

			if (selecionado != null && !popupDesabilitado) {
				arvorePopup.show(Arvore.this, e.getX(), e.getY());
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
		private Action atualizarAcao = Action.actionIcon("label.atualizar", Icones.ATUALIZAR);
		private Action excluirAcao = Action.actionMenu("label.excluir", Icones.EXCLUIR);

		public ArvorePopup() {
			add(new MenuItem(atualizarAcao));
			addSeparator();
			add(new MenuItem(abrirFormAcao));
			add(new MenuItem(abrirFichAcao));
			addSeparator();
			add(new MenuItem(excluirAcao));

			abrirFormAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirFormArquivo(Arvore.this)));
			abrirFichAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirFichArquivo(Arvore.this)));
			atualizarAcao.setActionListener(e -> ouvintes.forEach(o -> o.atualizarArvore(Arvore.this)));
			excluirAcao.setActionListener(e -> ouvintes.forEach(o -> o.excluirArquivo(Arvore.this)));
		}
	}
}