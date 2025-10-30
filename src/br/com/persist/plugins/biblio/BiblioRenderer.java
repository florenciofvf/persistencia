package br.com.persist.plugins.biblio;

import java.awt.Component;
import java.io.File;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

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
		TableModel model = table.getModel();
		if (model instanceof BiblioModelo) {
			File file = BiblioProvedor.getBiblio(row).getFile();
			setIcon(file.exists() ? Icones.SUCESSO : Icones.EXCEPTION);
		}
		return this;
	}
}