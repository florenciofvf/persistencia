package br.com.persist.plugins.anexo;

import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

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
			Font font = getFont();
			if (font != null) {
				Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
				attributes.put(TextAttribute.UNDERLINE, obj.isPadraoAbrir() ? TextAttribute.UNDERLINE_ON : -1);
				Font deriveFont = font.deriveFont(attributes);
				setFont(deriveFont);
			}
		}
		return this;
	}
}