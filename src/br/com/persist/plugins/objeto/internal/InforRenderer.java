package br.com.persist.plugins.objeto.internal;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import br.com.persist.assistencia.Util;

public class InforRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value != null && !Util.isEmpty(value.toString())) {
			setBackground(Color.YELLOW);
		} else {
			setBackground(Color.WHITE);
		}
		setForeground(Color.BLACK);
		return this;
	}
}