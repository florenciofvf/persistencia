package br.com.persist.plugins.requisicao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class RequisicaoModelo implements TableModel {
	private static final String[] COLUNAS = { "RESUMO", "URL" };
	private final List<Requisicao> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();

	public Requisicao getRequisicao(int i) {
		if (i >= 0 && i < lista.size()) {
			return lista.get(i);
		}
		return null;
	}

	public int adicionar(Requisicao req) {
		if (req != null) {
			lista.add(req);
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
		return true;
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
		} else if (columnIndex == 1) {
			req.setUrl(aValue.toString());
		}
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