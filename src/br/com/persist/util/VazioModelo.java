package br.com.persist.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class VazioModelo implements TableModel {
	private static final Logger LOG = Logger.getGlobal();

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
		LOG.log(Level.FINEST, "setValueAt");
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		LOG.log(Level.FINEST, "addTableModelListener");
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		LOG.log(Level.FINEST, "removeTableModelListener");
	}
}