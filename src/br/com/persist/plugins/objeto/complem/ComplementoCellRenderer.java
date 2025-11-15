package br.com.persist.plugins.objeto.complem;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ComplementoCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value != null) {
			String string = value.toString();
			if (string.length() > 0 && string.charAt(0) != '#') {
				setForeground(Color.BLUE);
			}
		}
		return this;
	}
}