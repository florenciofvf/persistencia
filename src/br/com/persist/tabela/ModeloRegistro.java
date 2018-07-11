package br.com.persist.tabela;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.persist.util.Util;

public class ModeloRegistro implements TableModel {
	private final List<List<Object>> registros;
	private final List<Coluna> colunas;
	private final boolean chaves;
	private final String tabela;

	public ModeloRegistro(List<Coluna> colunas, List<List<Object>> registros, String tabela) {
		this.registros = registros;
		this.colunas = colunas;
		this.tabela = tabela;
		int total = 0;
		for (Coluna c : colunas) {
			if (c.isChave()) {
				total++;
			}
		}
		chaves = total > 0;
	}

	public boolean isChaves() {
		return chaves;
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

		if (chaves) {
			try {
				Coluna coluna = colunas.get(columnIndex);
				String update = gerarUpdate(registro, coluna, aValue);
				Persistencia.executar(update);
				registro.set(columnIndex, aValue);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("UPDATE", ex, null);
			}
		} else {
			registro.set(columnIndex, aValue);
		}
	}

	public void excluir(int rowIndex) {
		List<Object> registro = registros.get(rowIndex);

		if (chaves) {
			try {
				String delete = gerarDelete(registro);
				Persistencia.executar(delete);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("DELETE", ex, null);
			}
		}
	}

	private String gerarUpdate(List<Object> registro, Coluna coluna, Object valor) {
		StringBuilder builder = new StringBuilder("UPDATE " + tabela);
		builder.append(" SET " + coluna.getNome() + " = " + coluna.get(valor));
		builder.append(getWhere(registro));

		return builder.toString();
	}

	private String gerarDelete(List<Object> registro) {
		StringBuilder builder = new StringBuilder("DELETE FROM " + tabela);
		builder.append(getWhere(registro));

		return builder.toString();
	}

	private String getWhere(List<Object> registro) {
		List<Coluna> lista = getChaves();
		Coluna coluna = lista.get(0);

		StringBuilder builder = new StringBuilder(" WHERE ");
		builder.append(coluna.getNome() + " = " + coluna.get(registro.get(coluna.getIndice())));

		for (int i = 1; i < lista.size(); i++) {
			coluna = lista.get(i);
			builder.append(" AND " + coluna.getNome() + " = " + coluna.get(registro.get(coluna.getIndice())));
		}

		return builder.toString();
	}

	private List<Coluna> getChaves() {
		return colunas.stream().filter(Coluna::isChave).collect(Collectors.toList());
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}