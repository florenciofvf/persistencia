package br.com.persist.assistencia;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private final Color cor;

	public CellRenderer(Color cor) {
		this.cor = cor;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (!isSelected) {
			setBackground(cor);
		}

		setForeground(Color.WHITE);

		return this;
	}
}