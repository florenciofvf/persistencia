package br.com.persist.plugins.persistencia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.table.AbstractTableModel;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.plugins.conexao.Conexao;

public class OrdenacaoModelo extends AbstractTableModel {
	private final transient PersistenciaModelo model;
	private static final long serialVersionUID = 1L;
	private transient Linha[] linhas;
	private boolean descendente;
	private boolean numero;
	private int coluna;

	public OrdenacaoModelo(PersistenciaModelo model) {
		this.model = Objects.requireNonNull(model);
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

	public int getTotalColunas() {
		return model.listarColunasTabela().size();
	}

	@Override
	public String getColumnName(int column) {
		return model.getColumnName(column);
	}

	public void atualizarSequencias(Map<String, String> mapaSequencia) {
		model.atualizarSequencias(mapaSequencia);
	}

	public String getNomeColunas(String apelido) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getColumnCount(); i++) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			if (!Util.isEmpty(apelido)) {
				sb.append(apelido + ".");
			}
			sb.append(getColumnName(i));
		}
		return sb.toString();
	}

	public List<String> getListaNomeColunasObrigatorias() {
		return model.getListaNomeColunasObrigatorias();
	}

	public List<String> getListaNomeColunasPreenchidas(boolean comChaves, int indice) {
		return model.getListaNomeColunasPreenchidas(comChaves, linhas[indice].indice);
	}

	public boolean contemCampoVazio(boolean comChaves, int indice) {
		return model.contemCampoVazio(comChaves, linhas[indice].indice);
	}

	public List<String> getListaNomeColunas(boolean comChaves) {
		return model.getListaNomeColunas(comChaves);
	}

	public List<Coluna> getColunas(List<String> nomes) {
		return model.getColunas(nomes);
	}

	public String detalhesExcecao() {
		return model.detalhesExcecao();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return model.getColumnClass(columnIndex);
	}

	public Coluna getColuna(int indice) {
		return model.getColuna(indice);
	}

	public Coluna getColuna(String nome) {
		return model.getColuna(nome);
	}

	public int getIndice(Coluna coluna) {
		return model.getIndice(coluna.getNome());
	}

	public int excluirRegistro(int rowIndex, String prefixoNomeTabela, boolean comWhere, Conexao conexao,
			AtomicBoolean atom) {
		return model.excluir(linhas[rowIndex].indice, prefixoNomeTabela, comWhere, conexao, atom);
	}

	public List<Object> getRegistro(int rowIndex) {
		return model.getRegistro(linhas[rowIndex].indice);
	}

	public void getDados(int rowIndex, StringBuilder sb, Coletor coletor, Conexao conexao) {
		model.getDados(linhas[rowIndex].indice, sb, coletor, conexao);
	}

	public Map<String, Object> getMap(int rowIndex, Coletor coletor) {
		return model.getMap(linhas[rowIndex].indice, coletor);
	}

	public String getUpdate(int rowIndex, String prefixoNomeTabela, Coletor coletor, boolean comWhere, Conexao conexao)
			throws PersistenciaException {
		return model.getUpdate(linhas[rowIndex].indice, prefixoNomeTabela, coletor, comWhere, conexao);
	}

	public String getUpdate(String prefixoNomeTabela, Coletor coletor, boolean comWhere, Conexao conexao)
			throws PersistenciaException {
		return model.getUpdate(prefixoNomeTabela, coletor, comWhere, conexao);
	}

	public String getDelete(int rowIndex, String prefixoNomeTabela, boolean comWhere, Conexao conexao)
			throws PersistenciaException {
		return model.getDelete(linhas[rowIndex].indice, prefixoNomeTabela, comWhere, conexao);
	}

	public String getDelete(String prefixoNomeTabela, boolean comWhere, Conexao conexao) throws PersistenciaException {
		return model.getDelete(prefixoNomeTabela, comWhere, conexao);
	}

	public String getInsert(int rowIndex, String prefixoNomeTabela, Coletor coletor) {
		return model.getInsert(linhas[rowIndex].indice, prefixoNomeTabela, coletor);
	}

	public String getInsert(String prefixoNomeTabela, Coletor coletor) {
		return model.getInsert(prefixoNomeTabela, coletor);
	}

	public List<IndiceValor> getValoresChaves(int rowIndex) {
		return model.getValoresChaves(linhas[rowIndex].indice);
	}

	public Map<String, String> getMapaChaves(int rowIndex, Conexao conexao) {
		return model.getMapaChaves(linhas[rowIndex].indice, conexao);
	}

	public List<Registro> listarRegistrosChave(List<Integer> indices, Conexao conexao) {
		List<Registro> registros = new ArrayList<>();
		List<Coluna> chaves = model.getChaves();
		for (Integer ind : indices) {
			Registro registro = model.criarRegistro(chaves, linhas[ind].indice, conexao);
			registros.add(registro);
		}
		return registros;
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
				Long valor = Util.isEmpty(string) ? 0 : Long.valueOf(string);
				Long outro = Util.isEmpty(outra) ? 0 : Long.valueOf(outra);
				if (descendente) {
					return valor.compareTo(outro);
				}
				return outro.compareTo(valor);
			} else {
				string = Util.isEmpty(string) ? Constantes.VAZIO : string;
				outra = Util.isEmpty(outra) ? Constantes.VAZIO : outra;
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