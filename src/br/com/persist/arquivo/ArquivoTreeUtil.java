package br.com.persist.arquivo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

public class ArquivoTreeUtil {
	private ArquivoTreeUtil() {
	}

	private static TreePath getTreePath(Arquivo arquivo) {
		List<Arquivo> caminho = new ArrayList<>();
		Arquivo o = arquivo;
		while (o != null) {
			caminho.add(0, o);
			o = o.getPai();
		}
		return new TreePath(caminho.toArray(new Object[] {}));
	}

	public static void atualizarEstrutura(ArquivoTree arquivoTree, Arquivo arquivo) {
		TreePath path = getTreePath(arquivo);
		TreeModelEvent event = new TreeModelEvent(arquivo, path);
		arquivoTree.getModelo().treeStructureChanged(event);
	}

	public static void refreshEstrutura(ArquivoTree arquivoTree, Arquivo arquivo) {
		TreePath path = getTreePath(arquivo);
		TreeModelEvent event = new TreeModelEvent(arquivo, path);
		arquivoTree.getModelo().treeNodesChanged(event);
	}

	public static void excluirEstrutura(ArquivoTree arquivoTree, Arquivo arquivo) {
		TreePath path = getTreePath(arquivo);
		TreeModelEvent event = new TreeModelEvent(arquivo, path);
		if (arquivo.getPai() != null) {
			arquivo.getPai().excluir(arquivo);
		}
		arquivoTree.getModelo().treeNodesRemoved(event);
		arquivoTree.setSelectionPath(null);
		SwingUtilities.updateComponentTreeUI(arquivoTree);
	}

	public static void selecionarObjeto(ArquivoTree arquivoTree, Arquivo arquivo) {
		TreePath path = getTreePath(arquivo);
		arquivoTree.expandPath(path);
		arquivoTree.makeVisible(path);
		arquivoTree.setSelectionPath(path);
		arquivoTree.scrollPathToVisible(path);
		SwingUtilities.updateComponentTreeUI(arquivoTree);
	}

	public static void reiniciarEstrutura(ArquivoTree arquivoTree) {
		ArquivoModelo modelo = arquivoTree.getModelo();
		arquivoTree.setModel(new ArquivoModelo((Arquivo) modelo.getRoot()));
	}

	public static File getRoot(ArquivoTree arquivoTree) {
		Object root = arquivoTree.getModel().getRoot();
		if (root instanceof Arquivo) {
			return ((Arquivo) root).getFile();
		}
		return null;
	}
}