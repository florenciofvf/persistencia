package br.com.persist.plugins.anotacao;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

public class AnotacaoTreeUtil {
	private AnotacaoTreeUtil() {
	}

	private static TreePath getTreePath(Anotacao anotacao) {
		List<Anotacao> caminho = new ArrayList<>();
		Anotacao o = anotacao;
		while (o != null) {
			caminho.add(0, o);
			o = o.getPai();
		}
		return new TreePath(caminho.toArray(new Object[] {}));
	}

	public static void atualizarEstrutura(AnotacaoTree anotacaoTree, Anotacao anotacao) {
		AnotacaoModelo modelo = (AnotacaoModelo) anotacaoTree.getModel();
		TreePath path = getTreePath(anotacao);
		TreeModelEvent event = new TreeModelEvent(anotacao, path);
		modelo.treeStructureChanged(event);
	}

	public static void refreshEstrutura(AnotacaoTree anotacaoTree, Anotacao anotacao) {
		AnotacaoModelo modelo = (AnotacaoModelo) anotacaoTree.getModel();
		TreePath path = getTreePath(anotacao);
		TreeModelEvent event = new TreeModelEvent(anotacao, path);
		modelo.treeNodesChanged(event);
	}

	public static void excluirEstrutura(AnotacaoTree anotacaoTree, Anotacao anotacao) {
		AnotacaoModelo modelo = (AnotacaoModelo) anotacaoTree.getModel();
		TreePath path = getTreePath(anotacao);
		TreeModelEvent event = new TreeModelEvent(anotacao, path);
		if (anotacao.getPai() != null) {
			anotacao.getPai().excluir(anotacao);
		}
		modelo.treeNodesRemoved(event);
		anotacaoTree.setSelectionPath(null);
		SwingUtilities.updateComponentTreeUI(anotacaoTree);
	}

	public static void selecionarObjeto(AnotacaoTree anotacaoTree, Anotacao anotacao) {
		TreePath path = getTreePath(anotacao);
		anotacaoTree.expandPath(path);
		anotacaoTree.makeVisible(path);
		anotacaoTree.setSelectionPath(path);
		anotacaoTree.scrollPathToVisible(path);
		SwingUtilities.updateComponentTreeUI(anotacaoTree);
	}
}