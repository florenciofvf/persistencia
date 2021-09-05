package br.com.persist.componente;

import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
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

	protected void checkPopupTrigger(MouseEvent e) {
		if (e.isPopupTrigger()) {
			TreePath clicado = getClosestPathForLocation(e.getX(), e.getY());
			if (clicado != null) {
				setSelectionPath(clicado);
			}
		}
	}
}