package br.com.persist.assistencia;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private final Color backGround;
	private final Color foreGround;

	public CellRenderer(Color backGround, Color foreGround) {
		this.backGround = backGround;
		this.foreGround = foreGround;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (!isSelected) {
			setBackground(backGround);
		}
		setForeground(foreGround);
		return this;
	}
}