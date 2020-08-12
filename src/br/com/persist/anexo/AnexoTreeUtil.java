package br.com.persist.anexo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import br.com.persist.arquivo.Arquivo;

public class AnexoTreeUtil {

	private AnexoTreeUtil() {
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

	public static void atualizarEstrutura(AnexoTree anexoTree, Arquivo arquivo) {
		AnexoModelo modelo = (AnexoModelo) anexoTree.getModel();

		TreePath path = getTreePath(arquivo);
		TreeModelEvent event = new TreeModelEvent(arquivo, path);

		modelo.treeStructureChanged(event);
	}

	public static void refreshEstrutura(AnexoTree anexoTree, Arquivo arquivo) {
		AnexoModelo modelo = (AnexoModelo) anexoTree.getModel();

		TreePath path = getTreePath(arquivo);
		TreeModelEvent event = new TreeModelEvent(arquivo, path);

		modelo.treeNodesChanged(event);
	}

	public static void excluirEstrutura(AnexoTree anexoTree, Arquivo arquivo) {
		AnexoModelo modelo = (AnexoModelo) anexoTree.getModel();

		TreePath path = getTreePath(arquivo);
		TreeModelEvent event = new TreeModelEvent(arquivo, path);

		if (arquivo.getPai() != null) {
			arquivo.getPai().excluir(arquivo);
		}

		modelo.treeNodesRemoved(event);
		anexoTree.setSelectionPath(null);
		SwingUtilities.updateComponentTreeUI(anexoTree);
	}

	public static void selecionarObjeto(AnexoTree anexoTree, Arquivo arquivo) {
		TreePath path = getTreePath(arquivo);

		anexoTree.expandPath(path);
		anexoTree.makeVisible(path);
		anexoTree.setSelectionPath(path);
		anexoTree.scrollPathToVisible(path);
		SwingUtilities.updateComponentTreeUI(anexoTree);
	}
}