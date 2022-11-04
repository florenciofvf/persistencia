package br.com.persist.plugins.metadado;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

public class MetadadoTreeUtil {
	private MetadadoTreeUtil() {
	}

	private static TreePath getTreePath(Metadado metadado) {
		List<Metadado> caminho = new ArrayList<>();
		Metadado o = metadado;
		while (o != null) {
			caminho.add(0, o);
			o = o.getPai();
		}
		return new TreePath(caminho.toArray(new Object[] {}));
	}

	public static void selecionarObjeto(MetadadoTree metadadoTree, Metadado metadado) {
		TreePath path = getTreePath(metadado);
		metadadoTree.expandPath(path);
		metadadoTree.makeVisible(path);
		metadadoTree.setSelectionPath(path);
		metadadoTree.scrollPathToVisible(path);
		SwingUtilities.updateComponentTreeUI(metadadoTree);
	}
}