package br.com.persist.plugins.anotacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class AnotacaoModelo implements TreeModel {
	private final EventListenerList listenerList = new EventListenerList();
	private static final Map<String, Anotacao> anotacoes = new HashMap<>();
	private static final File anotacaoRaiz = new File(AnotacaoConstantes.ANOTACOES);
	public static final File anotacaoInfo = new File(anotacaoRaiz, "A");
	private static final File ignore = new File(anotacaoRaiz, "ignore");
	private static final List<String> ignorados = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private final Anotacao raiz;

	public AnotacaoModelo() {
		this(new Anotacao(anotacaoRaiz));
	}

	public AnotacaoModelo(Anotacao raiz) {
		this.raiz = Objects.requireNonNull(raiz);
		inicializar();
		raiz.inflar(new StringBuilder());
	}

	private void inicializar() {
		anotacoes.clear();
		iniIgnorados();
		if (anotacaoInfo.isFile()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(anotacaoInfo), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				Anotacao selecionado = null;
				while (linha != null) {
					if (linha.startsWith(Constantes.SEP)) {
						selecionado = new Anotacao(new File(linha));
						anotacoes.put(linha, selecionado);
					} else {
						configurar(selecionado, linha);
					}
					linha = br.readLine();
				}
			} catch (Exception e) {
				LOG.log(Level.FINEST, "AnotacaoModelo.inicializar");
			}
		}
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
				LOG.log(Level.FINEST, "AnotacaoModelo.iniIgnorados()");
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

	private void configurar(Anotacao selecionado, String linha) {
		if (selecionado == null || linha == null) {
			return;
		}
		LOG.log(Level.FINEST, "configurar()");
	}

	public static Map<String, Anotacao> getAnotacoes() {
		return anotacoes;
	}

	@Override
	public Object getRoot() {
		return raiz;
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((Anotacao) parent).getAnotacao(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((Anotacao) parent).getTotal();
	}

	@Override
	public boolean isLeaf(Object parent) {
		return ((Anotacao) parent).estaVazio();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((Anotacao) parent).getIndice((Anotacao) child);
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