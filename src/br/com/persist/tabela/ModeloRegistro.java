package br.com.persist.tabela;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ModeloRegistro implements TableModel {
	private final List<List<Object>> registros;
	private final List<Coluna> colunas;

	public ModeloRegistro(List<Coluna> colunas, List<List<Object>> registros) {
		this.registros = registros;
		this.colunas = colunas;
	}

	@Override
	public int getRowCount() {
		return registros.size();
	}

	@Override
	public int getColumnCount() {
		return colunas.size();
	}

	public List<Coluna> getColunas() {
		return colunas;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colunas.get(columnIndex).getNome();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return !colunas.get(columnIndex).isChave();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		List<Object> registro = registros.get(rowIndex);
		return registro.get(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		List<Object> registro = registros.get(rowIndex);
		registro.set(columnIndex, aValue);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}