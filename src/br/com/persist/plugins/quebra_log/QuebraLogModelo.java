package br.com.persist.plugins.quebra_log;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

public class QuebraLogModelo extends AbstractTableModel {
	private final transient List<QuebraLog> lista = new ArrayList<>();
	private static final String[] COLUNAS = { "NOME", "ABSOLUTE PATH", "TAMANHO" };
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;

	public QuebraLog getQuebraLog(int i) {
		if (i >= 0 && i < lista.size()) {
			return lista.get(i);
		}
		return null;
	}

	public int adicionar(QuebraLog quebraLog) {
		if (quebraLog != null) {
			lista.add(quebraLog);
			return lista.size() - 1;
		}
		return -1;
	}

	@Override
	public int getRowCount() {
		return lista.size();
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
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		QuebraLog quebraLog = lista.get(rowIndex);
		if (columnIndex == 0) {
			return quebraLog.getNome();
		} else if (columnIndex == 1) {
			return quebraLog.getAbsolutePath();
		} else if (columnIndex == 2) {
			return quebraLog.getTamanho();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		LOG.log(Level.FINEST, "setValueAt");
	}
}