package br.com.persist.plugins.mapeamento;

import javax.swing.table.AbstractTableModel;

import br.com.persist.assistencia.Constantes;

public class MapeamentoModelo extends AbstractTableModel {
	private static final String[] COLUNAS = { "NOME", "VALOR" };
	private static final long serialVersionUID = 1L;

	@Override
	public int getRowCount() {
		return MapeamentoProvedor.getSize();
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
		Mapeamento m = MapeamentoProvedor.getMapeamento(rowIndex);
		switch (columnIndex) {
		case 0:
			return m.getNome();
		case 1:
			return m.getValor();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		String valor = aValue == null ? Constantes.VAZIO : aValue.toString();
		Mapeamento v = MapeamentoProvedor.getMapeamento(rowIndex);
		if (1 == columnIndex) {
			v.setValor(valor);
		}
	}
}