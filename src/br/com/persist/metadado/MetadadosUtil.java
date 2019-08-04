package br.com.persist.metadado;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import br.com.persist.Metadado;

public class MetadadosUtil {

	private MetadadosUtil() {
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

	public static void selecionarObjeto(Metadados metadados, Metadado metadado) {
		TreePath path = getTreePath(metadado);

		metadados.expandPath(path);
		metadados.makeVisible(path);
		metadados.setSelectionPath(path);
		metadados.scrollPathToVisible(path);
		SwingUtilities.updateComponentTreeUI(metadados);
	}
}