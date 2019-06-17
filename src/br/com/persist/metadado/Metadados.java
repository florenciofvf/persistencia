package br.com.persist.metadado;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import br.com.persist.Metadado;
import br.com.persist.modelo.MetadadoModelo;

public class Metadados extends JTree {
	private static final long serialVersionUID = 1L;

	public Metadados() {
		this(new MetadadoModelo());
	}

	public Metadados(TreeModel newModel) {
		super(newModel);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setBorder(BorderFactory.createEmptyBorder());
		setShowsRootHandles(true);
		setRootVisible(true);
	}

	public Metadado getObjetoSelecionado() {
		TreePath path = getSelectionPath();

		if (path == null) {
			return null;
		}

		if (path.getLastPathComponent() instanceof Metadado) {
			return (Metadado) path.getLastPathComponent();
		}

		return null;
	}
}