package br.com.persist.componente;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

public class Tree extends JTree {
	private static final long serialVersionUID = 1L;
	protected boolean popupTrigger;

	public Tree(TreeModel newModel) {
		super(newModel);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setBorder(BorderFactory.createEmptyBorder());
		setShowsRootHandles(true);
		setRootVisible(true);
	}
}