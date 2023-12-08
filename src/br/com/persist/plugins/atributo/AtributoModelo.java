package br.com.persist.plugins.atributo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class AtributoModelo extends AbstractTableModel {
	private static final Class<?>[] COLUNAS_CLASS = { String.class, Boolean.class };
	private final transient List<Atributo> lista = new ArrayList<>();
	private static final String[] COLUNAS = { "NOME", "IGNORAR" };
	private static final long serialVersionUID = 1L;

	public Atributo getAtributo(int i) {
		if (i >= 0 && i < lista.size()) {
			return lista.get(i);
		}
		return null;
	}

	public int adicionar(Atributo atributo) {
		if (atributo != null) {
			lista.add(atributo);
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
		return COLUNAS_CLASS[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Atributo atributo = lista.get(rowIndex);
		if (columnIndex == 0) {
			return atributo.getNome();
		} else if (columnIndex == 1) {
			return atributo.isIgnorar();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue == null) {
			return;
		}
		Atributo atributo = lista.get(rowIndex);
		if (columnIndex == 0) {
			atributo.setNome(aValue.toString());
		} else if (columnIndex == 1 && aValue instanceof Boolean) {
			atributo.setIgnorar(((Boolean) aValue).booleanValue());
		}
	}
}