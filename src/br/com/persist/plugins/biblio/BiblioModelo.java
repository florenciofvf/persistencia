package br.com.persist.plugins.biblio;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class BiblioModelo extends AbstractTableModel {
	private final transient List<Biblio> lista = new ArrayList<>();
	private static final String[] COLUNAS = { "NOME" };
	private static final long serialVersionUID = 1L;

	public Biblio getBiblio(int i) {
		if (i >= 0 && i < lista.size()) {
			return lista.get(i);
		}
		return null;
	}

	public int adicionar(Biblio biblio) {
		if (biblio != null) {
			lista.add(biblio);
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
		Biblio biblio = lista.get(rowIndex);
		if (columnIndex == 0) {
			return biblio.getNome();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue == null) {
			return;
		}
		Biblio biblio = lista.get(rowIndex);
		if (columnIndex == 0) {
			biblio.setNome(aValue.toString());
		}
	}
}