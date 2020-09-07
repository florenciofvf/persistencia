package br.com.persist.plugins.fragmento;

import javax.swing.table.AbstractTableModel;

import br.com.persist.util.Constantes;

public class FragmentoModelo extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final String[] COLUNAS = { "RESUMO", "GRUPO", "VALOR" };

	@Override
	public int getRowCount() {
		return FragmentoProvedor.getSize();
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
		Fragmento f = FragmentoProvedor.getFragmento(rowIndex);

		switch (columnIndex) {
		case 0:
			return f.getResumo();
		case 1:
			return f.getGrupo();
		case 2:
			return f.getValor();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		String valor = aValue == null ? Constantes.VAZIO : aValue.toString();
		Fragmento f = FragmentoProvedor.getFragmento(rowIndex);

		if (2 == columnIndex) {
			f.setValor(valor);
		}
	}
}