package br.com.persist.plugins.execucao;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.componente.Action;
import br.com.persist.componente.Popup;
import br.com.persist.componente.Tree;

public class ContainerTree extends Tree {
	private final transient List<ContainerTreeListener> ouvintes;
	private ContainerPopup popup = new ContainerPopup();
	private static final long serialVersionUID = 1L;

	public ContainerTree(ContainerModelo modelo) {
		super(modelo);
		addMouseListener(mouseListenerInner);
		addKeyListener(keyListenerInner);
		ouvintes = new ArrayList<>();
	}

	public ContainerTree() {
		this(new ContainerModelo(new Container()));
	}

	public void adicionarOuvinte(ContainerTreeListener listener) {
		if (listener != null) {
			ouvintes.add(listener);
		}
	}

	public ContainerModelo getModelo() {
		return (ContainerModelo) getModel();
	}

	public Container getRaiz() {
		return (Container) getModelo().getRoot();
	}

	public Container getObjetoSelecionado() {
		TreePath path = getSelectionPath();
		if (path == null) {
			return null;
		}
		if (path.getLastPathComponent() instanceof Container) {
			return (Container) path.getLastPathComponent();
		}
		return null;
	}

	private transient KeyAdapter keyListenerInner = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				mouseListenerInner
						.mouseClicked(new MouseEvent(ContainerTree.this, 0, 0, 0, 0, 0, Constantes.DOIS, false));
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
				if (selecionado.getLastPathComponent() instanceof Container) {
					Container container = (Container) selecionado.getLastPathComponent();
					popup.preShow(container);
					popup.show(ContainerTree.this, e.getX(), e.getY());
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
				Container arquivo = getObjetoSelecionado();
				if (arquivo == null) {
					return;
				}
				ouvintes.forEach(o -> o.executar(ContainerTree.this, true));
			}
		}
	};

	private class ContainerPopup extends Popup {
		private Action executarVarAcao = Action.acaoMenu(ExecucaoMensagens.getString("label.executar_var"),
				Icones.EXECUTAR);
		private Action executarAcao = Action.actionMenu("label.executar", Icones.EXECUTAR);
		private static final long serialVersionUID = 1L;

		private ContainerPopup() {
			addMenuItem(executarVarAcao);
			addMenuItem(executarAcao);
			executarAcao.setActionListener(e -> ouvintes.forEach(o -> o.executar(ContainerTree.this, false)));
			executarVarAcao.setActionListener(e -> ouvintes.forEach(o -> o.executarVar(ContainerTree.this)));
		}

		private void preShow(Container container) {
			executarVarAcao.setEnabled(container != null);
			executarAcao.setEnabled(container != null);
		}
	}
}