package br.com.persist.modelo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import br.com.persist.tabela.Coluna;
import br.com.persist.tabela.IndiceValor;
import br.com.persist.util.Util;

public class OrdenacaoModelo extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final transient TableModel model;
	private transient Linha[] linhas;
	private boolean descendente;
	private boolean numero;
	private int coluna;

	public OrdenacaoModelo(TableModel model) {
		Objects.requireNonNull(model);
		this.model = model;
		iniArray();
	}

	public void iniArray() {
		linhas = new Linha[model.getRowCount()];

		for (int i = 0; i < linhas.length; i++) {
			linhas[i] = new Linha(i);
		}
	}

	public void ordenar(int coluna, boolean numero, boolean descendente) {
		this.descendente = descendente;
		this.coluna = coluna;
		this.numero = numero;

		Arrays.sort(linhas);
		fireTableDataChanged();
	}

	public TableModel getModel() {
		return model;
	}

	@Override
	public int getRowCount() {
		return model.getRowCount();
	}

	@Override
	public int getColumnCount() {
		return model.getColumnCount();
	}

	@Override
	public String getColumnName(int column) {
		return model.getColumnName(column);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return model.getColumnClass(columnIndex);
	}

	public Coluna getColuna(int indice) {
		if (model instanceof RegistroModelo) {
			return ((RegistroModelo) model).getColuna(indice);
		}

		return null;
	}

	public int excluirRegistro(int rowIndex) {
		RegistroModelo modelo = (RegistroModelo) model;
		return modelo.excluir(linhas[rowIndex].indice);
	}

	public List<Object> getRegistro(int rowIndex) {
		RegistroModelo modelo = (RegistroModelo) model;
		return modelo.getRegistro(linhas[rowIndex].indice);
	}

	public void getDados(int rowIndex, StringBuilder sb) {
		RegistroModelo modelo = (RegistroModelo) model;
		modelo.getDados(linhas[rowIndex].indice, sb);
	}

	public String getUpdate(int rowIndex) {
		RegistroModelo modelo = (RegistroModelo) model;
		return modelo.getUpdate(linhas[rowIndex].indice);
	}

	public String getUpdate() {
		RegistroModelo modelo = (RegistroModelo) model;
		return modelo.getUpdate();
	}

	public String getDelete(int rowIndex) {
		RegistroModelo modelo = (RegistroModelo) model;
		return modelo.getDelete(linhas[rowIndex].indice);
	}

	public String getInsert(int rowIndex) {
		RegistroModelo modelo = (RegistroModelo) model;
		return modelo.getInsert(linhas[rowIndex].indice);
	}

	public String getInsert() {
		RegistroModelo modelo = (RegistroModelo) model;
		return modelo.getInsert();
	}

	public List<IndiceValor> getValoresChaves(int rowIndex) {
		RegistroModelo modelo = (RegistroModelo) model;
		return modelo.getValoresChaves(linhas[rowIndex].indice);
	}

	public Map<String, String> getMapaChaves(int rowIndex) {
		RegistroModelo modelo = (RegistroModelo) model;
		return modelo.getMapaChaves(linhas[rowIndex].indice);
	}

	public void excluirValoresChaves(List<List<IndiceValor>> listaValores) {
		RegistroModelo modelo = (RegistroModelo) model;

		for (List<IndiceValor> lista : listaValores) {
			modelo.excluirValoresChaves(lista);
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return model.getValueAt(linhas[rowIndex].indice, columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return model.isCellEditable(linhas[rowIndex].indice, columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		model.setValueAt(aValue, linhas[rowIndex].indice, columnIndex);
	}

	private class Linha implements Comparable<Linha> {
		private final int indice;

		Linha(int indice) {
			this.indice = indice;
		}

		@Override
		public int compareTo(Linha o) {
			String string = (String) model.getValueAt(indice, coluna);
			String outra = (String) model.getValueAt(o.indice, coluna);

			if (numero) {
				Long valor = Util.estaVazio(string) ? 0 : Long.valueOf(string);
				Long outro = Util.estaVazio(outra) ? 0 : Long.valueOf(outra);

				if (descendente) {
					return valor.compareTo(outro);
				}

				return outro.compareTo(valor);
			} else {
				string = Util.estaVazio(string) ? "" : string;
				outra = Util.estaVazio(outra) ? "" : outra;

				if (descendente) {
					return string.compareTo(outra);
				}

				return outra.compareTo(string);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}

			if (obj instanceof Linha) {
				Linha outro = (Linha) obj;
				return indice == outro.indice;
			}

			return false;
		}

		@Override
		public int hashCode() {
			return indice;
		}
	}
}