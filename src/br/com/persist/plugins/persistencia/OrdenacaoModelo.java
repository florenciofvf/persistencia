package br.com.persist.plugins.persistencia;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.table.AbstractTableModel;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class OrdenacaoModelo extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final transient PersistenciaModelo model;
	private transient Linha[] linhas;
	private boolean descendente;
	private boolean numero;
	private int coluna;

	public OrdenacaoModelo(PersistenciaModelo model) {
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

	public PersistenciaModelo getModelo() {
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

	public String getNomeColunas() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < getColumnCount(); i++) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(getColumnName(i));
		}

		return sb.toString();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return model.getColumnClass(columnIndex);
	}

	public Coluna getColuna(int indice) {
		return model.getColuna(indice);
	}

	public int excluirRegistro(int rowIndex, String prefixoNomeTabela) {
		return model.excluir(linhas[rowIndex].indice, prefixoNomeTabela);
	}

	public List<Object> getRegistro(int rowIndex) {
		return model.getRegistro(linhas[rowIndex].indice);
	}

	public void getDados(int rowIndex, StringBuilder sb) {
		model.getDados(linhas[rowIndex].indice, sb);
	}

	public String getUpdate(int rowIndex, String prefixoNomeTabela) {
		return model.getUpdate(linhas[rowIndex].indice, prefixoNomeTabela);
	}

	public String getUpdate(String prefixoNomeTabela) {
		return model.getUpdate(prefixoNomeTabela);
	}

	public String getDelete(int rowIndex, String prefixoNomeTabela) {
		return model.getDelete(linhas[rowIndex].indice, prefixoNomeTabela);
	}

	public String getDelete(String prefixoNomeTabela) {
		return model.getDelete(prefixoNomeTabela);
	}

	public String getInsert(int rowIndex, String prefixoNomeTabela) {
		return model.getInsert(linhas[rowIndex].indice, prefixoNomeTabela);
	}

	public String getInsert(String prefixoNomeTabela) {
		return model.getInsert(prefixoNomeTabela);
	}

	public List<IndiceValor> getValoresChaves(int rowIndex) {
		return model.getValoresChaves(linhas[rowIndex].indice);
	}

	public Map<String, String> getMapaChaves(int rowIndex) {
		return model.getMapaChaves(linhas[rowIndex].indice);
	}

	public void excluirValoresChaves(List<List<IndiceValor>> listaValores) {
		for (List<IndiceValor> lista : listaValores) {
			model.excluirValoresChaves(lista);
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

		private Linha(int indice) {
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
				string = Util.estaVazio(string) ? Constantes.VAZIO : string;
				outra = Util.estaVazio(outra) ? Constantes.VAZIO : outra;

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