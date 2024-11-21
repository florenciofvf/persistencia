package br.com.persist.plugins.projeto;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.assistencia.Icones;

public class ProjetoRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof Arquivo) {
			Arquivo obj = (Arquivo) value;
			if (obj.isFile()) {
				setIcon(Icones.EXECUTAR);
			} else if (obj.getNivel() == 0) {
				setIcon(Icones.INFO);
			} else if (obj.getNivel() == 1) {
				setIcon(Icones.CRIAR);
			} else if (obj.getNivel() == 2) {
				setIcon(Icones.INFO);
			} else if (obj.getNivel() == 3) {
				setIcon(Icones.URL);
			}
		}
		return this;
	}
}