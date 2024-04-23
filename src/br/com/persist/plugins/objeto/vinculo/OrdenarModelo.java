package br.com.persist.plugins.objeto.vinculo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import br.com.persist.assistencia.Util;

public class OrdenarModelo extends AbstractTableModel {
	private static final String[] COLUNAS = { "ORDEM", "NOME" };
	private static final long serialVersionUID = 1L;
	private final transient List<Pesquisa> lista;

	public OrdenarModelo(List<Pesquisa> lista) {
		this.lista = lista == null ? new ArrayList<>() : lista;
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
		return columnIndex == 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Pesquisa pesquisa = lista.get(rowIndex);
		if (columnIndex == 0) {
			return String.valueOf(pesquisa.getOrdem());
		} else if (columnIndex == 1) {
			return pesquisa.getNomeParaMenuItem();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue == null) {
			return;
		}
		Pesquisa pesquisa = lista.get(rowIndex);
		if (columnIndex == 0) {
			String string = aValue.toString();
			if (!Util.isEmpty(string)) {
				pesquisa.setOrdem(Util.getInt(string, pesquisa.getOrdem()));
			}
		}
	}
}