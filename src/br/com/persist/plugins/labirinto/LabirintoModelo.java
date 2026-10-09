package br.com.persist.plugins.labirinto;

import java.util.Set;

import br.com.persist.abstrato.AbstratoTableModel;
import br.com.persist.assistencia.BuscaConteudo;
import br.com.persist.assistencia.Constantes;

public class LabirintoModelo extends AbstratoTableModel implements BuscaConteudo {
	private static final String[] COLUNAS = { "NOME", "VALOR" };
	private static final long serialVersionUID = 1L;

	@Override
	public int getRowCount() {
		return LabirintoProvedor.getSize();
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
		Labirinto item = LabirintoProvedor.getLabirinto(rowIndex);
		if (columnIndex == 0) {
			return item.getNome();
		} else if (columnIndex == 1) {
			return item.getValor();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		String valor = aValue == null ? Constantes.VAZIO : aValue.toString();
		Labirinto item = LabirintoProvedor.getLabirinto(rowIndex);
		if (columnIndex == 0) {
			item.setNome(valor);
		} else if (columnIndex == 1) {
			item.setValor(valor);
		}
	}

	@Override
	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		LabirintoProvedor.contemConteudo(set, string, porParte);
	}
}