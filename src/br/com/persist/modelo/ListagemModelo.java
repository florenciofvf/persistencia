package br.com.persist.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.persist.tabela.Coluna;

public class ListagemModelo implements TableModel {
	private static final Logger LOG = Logger.getGlobal();
	private final List<Coluna> colunasInfo = new ArrayList<>();
	private final List<List<String>> dados;
	private final List<String> colunas;

	public ListagemModelo(List<String> colunas, List<List<String>> dados) {
		this.colunas = colunas;
		this.dados = dados;

		int i = 0;

		for (String s : colunas) {
			colunasInfo.add(new Coluna(s, i));
			i++;
		}
	}

	public List<List<String>> getDados() {
		return dados;
	}

	public List<Coluna> getColunasInfo() {
		return colunasInfo;
	}

	public List<String> getColunas() {
		return colunas;
	}

	@Override
	public int getRowCount() {
		return dados.size();
	}

	@Override
	public int getColumnCount() {
		return colunas.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colunas.get(columnIndex);
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
		return dados.get(rowIndex).get(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		LOG.log(Level.FINEST, "setValueAt");
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