package br.com.persist.plugins.anexo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

public class AnexoTreeUtil {

	private AnexoTreeUtil() {
	}

	private static TreePath getTreePath(Anexo anexo) {
		List<Anexo> caminho = new ArrayList<>();

		Anexo o = anexo;

		while (o != null) {
			caminho.add(0, o);
			o = o.getPai();
		}

		return new TreePath(caminho.toArray(new Object[] {}));
	}

	public static void atualizarEstrutura(AnexoTree anexoTree, Anexo anexo) {
		AnexoModelo modelo = (AnexoModelo) anexoTree.getModel();

		TreePath path = getTreePath(anexo);
		TreeModelEvent event = new TreeModelEvent(anexo, path);

		modelo.treeStructureChanged(event);
	}

	public static void refreshEstrutura(AnexoTree anexoTree, Anexo anexo) {
		AnexoModelo modelo = (AnexoModelo) anexoTree.getModel();

		TreePath path = getTreePath(anexo);
		TreeModelEvent event = new TreeModelEvent(anexo, path);

		modelo.treeNodesChanged(event);
	}

	public static void excluirEstrutura(AnexoTree anexoTree, Anexo anexo) {
		AnexoModelo modelo = (AnexoModelo) anexoTree.getModel();

		TreePath path = getTreePath(anexo);
		TreeModelEvent event = new TreeModelEvent(anexo, path);

		if (anexo.getPai() != null) {
			anexo.getPai().excluir(anexo);
		}

		modelo.treeNodesRemoved(event);
		anexoTree.setSelectionPath(null);
		SwingUtilities.updateComponentTreeUI(anexoTree);
	}

	public static void selecionarObjeto(AnexoTree anexoTree, Anexo anexo) {
		TreePath path = getTreePath(anexo);

		anexoTree.expandPath(path);
		anexoTree.makeVisible(path);
		anexoTree.setSelectionPath(path);
		anexoTree.scrollPathToVisible(path);
		SwingUtilities.updateComponentTreeUI(anexoTree);
	}
}