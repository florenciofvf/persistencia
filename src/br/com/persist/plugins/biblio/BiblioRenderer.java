package br.com.persist.plugins.biblio;

import java.awt.Component;
import java.io.File;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import br.com.persist.assistencia.Icones;

public class BiblioRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public BiblioRenderer() {
		setHorizontalAlignment(CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value instanceof Biblio) {
			File file = ((Biblio) value).getFile();
			if (file.exists()) {
				setIcon(Icones.SUCESSO);
			} else {
				setIcon(null);
			}
		}
		return this;
	}
}