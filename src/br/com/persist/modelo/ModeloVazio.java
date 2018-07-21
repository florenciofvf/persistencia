package br.com.persist.modelo;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ModeloVazio implements TableModel {

	public ModeloVazio() {
	}

	@Override
	public int getRowCount() {
		return 0;
	}

	@Override
	public int getColumnCount() {
		return 0;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}