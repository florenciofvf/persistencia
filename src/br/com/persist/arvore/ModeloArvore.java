package br.com.persist.arvore;

import java.io.File;
import java.util.Objects;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ModeloArvore implements TreeModel {
	private final EventListenerList listenerList = new EventListenerList();
	private final Arquivo raiz;

	public ModeloArvore() {
		this(new Arquivo(new File("arquivos")));
	}

	public ModeloArvore(Arquivo raiz) {
		Objects.requireNonNull(raiz);
		this.raiz = raiz;
	}

	@Override
	public Object getRoot() {
		return raiz;
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((Arquivo) parent).getArquivo(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((Arquivo) parent).getTotal();
	}

	@Override
	public boolean isLeaf(Object parent) {
		return ((Arquivo) parent).estaVazio();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((Arquivo) parent).getIndice((Arquivo) child);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

	public void treeStructureChanged(TreeModelEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(event);
			}
		}
	}

	public void treeNodesRemoved(TreeModelEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				((TreeModelListener) listeners[i + 1]).treeNodesRemoved(event);
			}
		}
	}

	public void treeNodesChanged(TreeModelEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				((TreeModelListener) listeners[i + 1]).treeNodesChanged(event);
			}
		}
	}
}