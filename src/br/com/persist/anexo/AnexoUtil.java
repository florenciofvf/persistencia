package br.com.persist.anexo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import br.com.persist.Arquivo;
import br.com.persist.modelo.AnexoModelo;

public class AnexoUtil {

	private AnexoUtil() {
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

	public static void atualizarEstrutura(Anexo anexo, Arquivo arquivo) {
		AnexoModelo modelo = (AnexoModelo) anexo.getModel();

		TreePath path = getTreePath(arquivo);
		TreeModelEvent event = new TreeModelEvent(arquivo, path);

		modelo.treeStructureChanged(event);
	}

	public static void refreshEstrutura(Anexo anexo, Arquivo arquivo) {
		AnexoModelo modelo = (AnexoModelo) anexo.getModel();

		TreePath path = getTreePath(arquivo);
		TreeModelEvent event = new TreeModelEvent(arquivo, path);

		modelo.treeNodesChanged(event);
	}

	public static void excluirEstrutura(Anexo anexo, Arquivo arquivo) {
		AnexoModelo modelo = (AnexoModelo) anexo.getModel();

		TreePath path = getTreePath(arquivo);
		TreeModelEvent event = new TreeModelEvent(arquivo, path);

		if (arquivo.getPai() != null) {
			arquivo.getPai().excluir(arquivo);
		}

		modelo.treeNodesRemoved(event);
		anexo.setSelectionPath(null);
		SwingUtilities.updateComponentTreeUI(anexo);
	}

	public static void selecionarObjeto(Anexo anexo, Arquivo arquivo) {
		TreePath path = getTreePath(arquivo);

		anexo.expandPath(path);
		anexo.makeVisible(path);
		anexo.setSelectionPath(path);
		anexo.scrollPathToVisible(path);
		SwingUtilities.updateComponentTreeUI(anexo);
	}
}