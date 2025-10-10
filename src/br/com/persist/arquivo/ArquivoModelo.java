package br.com.persist.arquivo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ArquivoModelo implements TreeModel {
	private final EventListenerList listenerList = new EventListenerList();
	private static final Logger LOG = Logger.getGlobal();
	private final Arquivo raiz;

	public ArquivoModelo() {
		this(new Arquivo(new File("."), new ArrayList<>()));
	}

	public ArquivoModelo(Arquivo raiz) {
		this.raiz = Objects.requireNonNull(raiz);
		raiz.inflar();
	}

	public Arquivo getArquivo(File file) {
		return raiz.getArquivo(file);
	}

	public void listar(List<Arquivo> lista) {
		raiz.listar(lista);
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
		LOG.log(Level.FINEST, "valueForPathChanged");
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