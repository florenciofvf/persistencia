package br.com.persist.plugins.anexo;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class AnexoRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof Anexo) {
			Anexo obj = (Anexo) value;
			if (obj.getIcone() != null) {
				setIcon(obj.getIcone());
			}
			if (obj.getCorFonte() != null) {
				setForeground(obj.getCorFonte());
			}
		}
		return this;
	}
}