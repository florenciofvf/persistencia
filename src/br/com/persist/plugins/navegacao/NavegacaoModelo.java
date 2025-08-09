package br.com.persist.plugins.navegacao;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class NavegacaoModelo extends AbstractTableModel {
	private final transient List<Navegacao> lista = new ArrayList<>();
	private static final String[] COLUNAS = { "NOME" };
	private static final long serialVersionUID = 1L;

	public Navegacao getNavegacao(int i) {
		if (i >= 0 && i < lista.size()) {
			return lista.get(i);
		}
		return null;
	}

	public int adicionar(Navegacao navegacao) {
		if (navegacao != null) {
			lista.add(navegacao);
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
		Navegacao navegacao = lista.get(rowIndex);
		if (columnIndex == 0) {
			return navegacao.getNome();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue == null) {
			return;
		}
		Navegacao navegacao = lista.get(rowIndex);
		if (columnIndex == 0) {
			navegacao.setNome(aValue.toString());
		}
	}
}