package br.com.persist.plugins.requisicao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class RequisicaoModelo implements TableModel {
	private static final String[] COLUNAS = { "DESCRICAO", "URL" };
	private final List<Requisicao> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();

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
		return columnIndex != 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Requisicao req = lista.get(rowIndex);
		if (columnIndex == 0) {
			return req.getDesc();
		}
		return req.getUrl();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue == null) {
			return;
		}
		Requisicao req = lista.get(rowIndex);
		if (columnIndex == 0) {
			req.setDesc(aValue.toString());
		}
		req.setUrl(aValue.toString());
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