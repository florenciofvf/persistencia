package br.com.persist.plugins.biblio;

import javax.swing.table.AbstractTableModel;

public class BiblioJarModelo extends AbstractTableModel {
	private static final String[] COLUNAS = { "NOME" };
	private static final long serialVersionUID = 1L;

	@Override
	public int getRowCount() {
		return BiblioJarProvedor.getSize();
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
		Biblio biblio = BiblioJarProvedor.getBiblio(rowIndex);
		if (columnIndex == 0) {
			return biblio.getNome();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue == null) {
			return;
		}
		Biblio biblio = BiblioJarProvedor.getBiblio(rowIndex);
		if (columnIndex == 0) {
			biblio.setNome(aValue.toString());
		}
	}
}