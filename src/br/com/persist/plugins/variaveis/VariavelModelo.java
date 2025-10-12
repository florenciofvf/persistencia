package br.com.persist.plugins.variaveis;

import java.util.Set;

import javax.swing.table.AbstractTableModel;

import br.com.persist.assistencia.BuscaConteudo;
import br.com.persist.assistencia.Constantes;

public class VariavelModelo extends AbstractTableModel implements BuscaConteudo {
	private static final String[] COLUNAS = { "NOME", "VALOR" };
	private static final long serialVersionUID = 1L;

	@Override
	public int getRowCount() {
		return VariavelProvedor.getSize();
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
		return 1 == columnIndex;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Variavel v = VariavelProvedor.getVariavel(rowIndex);
		switch (columnIndex) {
		case 0:
			return v.getNome();
		case 1:
			return v.getValor();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		String valor = aValue == null ? Constantes.VAZIO : aValue.toString();
		Variavel v = VariavelProvedor.getVariavel(rowIndex);
		if (1 == columnIndex) {
			v.setValor(valor);
		}
	}

	@Override
	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		VariavelProvedor.contemConteudo(set, string, porParte);
	}
}