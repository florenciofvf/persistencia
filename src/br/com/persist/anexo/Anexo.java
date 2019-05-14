package br.com.persist.anexo;

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
import br.com.persist.listener.AnexoListener;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.MenuPadrao1;

public class Anexo extends JTree {
	private static final long serialVersionUID = 1L;
	private final transient List<AnexoListener> ouvintes;
	private AnexoPopup anexoPopup = new AnexoPopup();
	private boolean popupDesabilitado;

	public Anexo(TreeModel newModel) {
		super(newModel);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setBorder(BorderFactory.createEmptyBorder());
		addMouseListener(mouseListenerInner);
		ouvintes = new ArrayList<>();
		setShowsRootHandles(true);
		setRootVisible(true);
	}

	public void adicionarOuvinte(AnexoListener listener) {
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

		AnexoUtil.selecionarObjeto(this, arquivo);
	}

	public void excluirSelecionado() {
		Arquivo selecionado = getObjetoSelecionado();

		if (selecionado == null) {
			return;
		}

		AnexoUtil.excluirEstrutura(this, selecionado);
	}

	private transient MouseListener mouseListenerInner = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				ouvintes.forEach(o -> o.abrirFichArquivo(Anexo.this));
			} else {
				ouvintes.forEach(o -> o.clickArquivo(Anexo.this));
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

			TreePath anexoCli = getClosestPathForLocation(e.getX(), e.getY());
			TreePath anexoSel = getSelectionPath();

			if (anexoCli == null || anexoSel == null) {
				setSelectionPath(null);
				return;
			}

			Rectangle rect = getPathBounds(anexoCli);

			if (rect == null || !rect.contains(e.getX(), e.getY())) {
				setSelectionPath(null);
				return;
			}

			if (anexoCli.equals(anexoSel)) {
				anexoPopup.show(Anexo.this, e.getX(), e.getY());
			} else {
				setSelectionPath(null);
			}
		}
	};

	public void desabilitarPopup() {
		popupDesabilitado = true;
	}

	private class AnexoPopup extends Popup {
		private static final long serialVersionUID = 1L;
		private Action selecionarAcao = Action.actionMenu("label.selecionar", Icones.CURSOR);
		private Action atualizarAcao = Action.actionMenu("label.status", Icones.ATUALIZAR);
		private Action fecharAcao = Action.actionMenu("label.fechar", Icones.FECHAR);

		public AnexoPopup() {
			add(new MenuAbrir());
			addMenuItem(true, selecionarAcao);
			addMenuItem(true, fecharAcao);
			addMenuItem(true, atualizarAcao);

			selecionarAcao.setActionListener(e -> ouvintes.forEach(o -> o.selecionarArquivo(Anexo.this)));
			atualizarAcao.setActionListener(e -> ouvintes.forEach(o -> o.atualizarArvore(Anexo.this)));
			fecharAcao.setActionListener(e -> ouvintes.forEach(o -> o.fecharArquivo(Anexo.this)));
		}

		class MenuAbrir extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrir() {
				super("label.abrir", Icones.ABRIR, false);

				formularioAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirFormArquivo(Anexo.this)));
				ficharioAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirFichArquivo(Anexo.this)));
			}
		}
	}
}