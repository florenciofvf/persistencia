package br.com.persist.arvore;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

public class ArvoreUtil {
	private ArvoreUtil() {
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

	public static void atualizarEstrutura(Arvore arvore, Arquivo arquivo) {
		ModeloArvore modelo = (ModeloArvore) arvore.getModel();

		TreePath path = getTreePath(arquivo);
		TreeModelEvent event = new TreeModelEvent(arquivo, path);

		modelo.treeStructureChanged(event);
	}

	public static void excluirEstrutura(Arvore arvore, Arquivo arquivo) {
		ModeloArvore modelo = (ModeloArvore) arvore.getModel();

		TreePath path = getTreePath(arquivo);
		TreeModelEvent event = new TreeModelEvent(arquivo, path);

		if (arquivo.getPai() != null) {
			arquivo.getPai().excluir(arquivo);
		}

		modelo.treeNodesRemoved(event);
		arvore.setSelectionPath(null);
		SwingUtilities.updateComponentTreeUI(arvore);
	}

	public static void selecionarObjeto(Arvore arvore, Arquivo arquivo) {
		TreePath path = getTreePath(arquivo);

		arvore.expandPath(path);
		arvore.makeVisible(path);
		arvore.setSelectionPath(path);
		arvore.scrollPathToVisible(path);
		SwingUtilities.updateComponentTreeUI(arvore);
	}
}