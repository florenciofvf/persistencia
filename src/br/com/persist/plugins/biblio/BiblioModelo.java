package br.com.persist.plugins.biblio;

import javax.swing.table.AbstractTableModel;

public class BiblioModelo extends AbstractTableModel {
	private static final String[] COLUNAS = { "EXISTENTE", "NOME" };
	private static final long serialVersionUID = 1L;

	@Override
	public int getRowCount() {
		return BiblioProvedor.getSize();
	}

	@Override
	public int getColumnCount() {
		return COLUNAS.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUNAS[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Biblio biblio = BiblioProvedor.getBiblio(rowIndex);
		if (columnIndex == 1) {
			return biblio.getNome();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue == null) {
			return;
		}
		Biblio biblio = BiblioProvedor.getBiblio(rowIndex);
		if (columnIndex == 1) {
			biblio.setNome(aValue.toString());
		}
	}
}