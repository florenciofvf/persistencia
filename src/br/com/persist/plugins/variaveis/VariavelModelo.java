package br.com.persist.plugins.variaveis;

import javax.swing.table.AbstractTableModel;

import br.com.persist.util.Constantes;

public class VariavelModelo extends AbstractTableModel {
	private static final String[] COLUNAS = { "NOME", "VALOR" };
	private static final long serialVersionUID = 1L;

	@Override
	public int getRowCount() {
		return VariavelProvedor.getSize();
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
		Variavel v = VariavelProvedor.getVariavel(rowIndex);

		switch (columnIndex) {
		case 0:
			return v.getNome();
		case 1:
			return v.getValor();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		String valor = aValue == null ? Constantes.VAZIO : aValue.toString();
		Variavel v = VariavelProvedor.getVariavel(rowIndex);

		if (1 == columnIndex) {
			v.setValor(valor);
		}
	}
}