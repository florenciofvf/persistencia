package br.com.persist.plugins.arquivo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

import br.com.persist.assistencia.Util;

public class ArquivoModelo implements TreeModel {
	private final EventListenerList listenerList = new EventListenerList();
	public static final File FILE = new File(ArquivoConstantes.ARQUIVOS);
	private static final List<String> ignorados = new ArrayList<>();
	private static final File ignore = new File(FILE, "ignore");
	private static final Logger LOG = Logger.getGlobal();
	private final Arquivo raiz;

	public ArquivoModelo() {
		this(new Arquivo(FILE));
	}

	public ArquivoModelo(Arquivo raiz) {
		Objects.requireNonNull(raiz);
		this.raiz = raiz;
		iniIgnorados();
		raiz.inflar();
	}

	private void iniIgnorados() {
		ignorados.clear();
		if (ignore.isFile()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(ignore), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				while (linha != null) {
					if (!Util.estaVazio(linha)) {
						ignorados.add(linha);
					}
					linha = br.readLine();
				}
			} catch (Exception e) {
				LOG.log(Level.FINEST, "ArquivoModelo.iniIgnorados()");
			}
		}
	}

	public static boolean ignorar(String string) {
		if (string != null) {
			for (String s : ignorados) {
				if (string.endsWith(s)) {
					return true;
				}
			}
		}
		return false;
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