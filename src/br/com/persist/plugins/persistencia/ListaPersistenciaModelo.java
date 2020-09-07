package br.com.persist.plugins.persistencia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.util.Constantes;
import br.com.persist.util.IndiceValor;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;

public class ListaPersistenciaModelo implements TableModel {
	private static final Logger LOG = Logger.getGlobal();
	private final List<List<Object>> registros;
	private final String prefixoNomeTabela;
	private final List<Coluna> colunas;
	private final boolean chaves;
	private final String tabela;
	private Conexao conexao;

	public ListaPersistenciaModelo(List<Coluna> colunas, List<List<Object>> registros, String tabela, Conexao conexao, String prefixoNomeTabela) {
		this.prefixoNomeTabela = prefixoNomeTabela;
		this.registros = registros;
		this.colunas = colunas;
		this.tabela = tabela;
		setConexao(conexao);
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

	public Conexao getConexao() {
		return conexao;
	}

	public void setConexao(Conexao conexao) {
		this.conexao = conexao;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colunas.get(columnIndex).getNome();
	}

	public Coluna getColuna(int indice) {
		return colunas.get(indice);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		Coluna coluna = colunas.get(columnIndex);
		return !coluna.isChave() && !coluna.isBlob() && !coluna.isColunaInfo();
	}

	public List<Object> getRegistro(int rowIndex) {
		return registros.get(rowIndex);
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
				String update = gerarUpdate(registro, new Coluna[] { coluna }, new Object[] { aValue },
						prefixoNomeTabela);
				Persistencia.executar(update, ConexaoProvedor.getConnection(conexao));
				registro.set(columnIndex, aValue);
				if (Preferencias.isAreaTransTabelaRegistros()) {
					Util.setContentTransfered(update);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("UPDATE", ex, null);
			}
		} else {
			registro.set(columnIndex, aValue);
		}
	}

	public void getDados(int rowIndex, StringBuilder sb) {
		List<Object> registro = registros.get(rowIndex);
		List<Object> valores = new ArrayList<>();

		for (Coluna coluna : colunas) {
			valores.add(registro.get(coluna.getIndice()));
		}

		getDado(colunas.toArray(new Coluna[0]), valores.toArray(new Object[0]), sb);
	}

	public String getUpdate(int rowIndex, String prefixoNomeTabela) {
		List<Object> registro = registros.get(rowIndex);

		if (chaves) {
			List<Object> valores = new ArrayList<>();
			List<Coluna> naoChaves = getNaoChaves();

			if (naoChaves.isEmpty()) {
				return null;
			}

			for (Coluna coluna : naoChaves) {
				valores.add(registro.get(coluna.getIndice()));
			}

			return gerarUpdate(registro, naoChaves.toArray(new Coluna[0]), valores.toArray(new Object[0]),
					prefixoNomeTabela);
		}

		return null;
	}

	public String getUpdate(String prefixoNomeTabela) {
		if (chaves) {
			List<Object> valores = new ArrayList<>();
			List<Coluna> naoChaves = getNaoChaves();

			if (naoChaves.isEmpty()) {
				return null;
			}

			for (Coluna coluna : naoChaves) {
				valores.add(coluna.getNome());
			}

			return gerarUpdate(null, naoChaves.toArray(new Coluna[0]), valores.toArray(new Object[0]),
					prefixoNomeTabela);
		}

		return null;
	}

	public String getDelete(int rowIndex, String prefixoNomeTabela) {
		List<Object> registro = registros.get(rowIndex);

		if (chaves) {
			return gerarDelete(registro, prefixoNomeTabela);
		}

		return null;
	}

	public String getDelete(String prefixoNomeTabela) {
		if (chaves) {
			return gerarDelete(null, prefixoNomeTabela);
		}

		return null;
	}

	public String getInsert(int rowIndex, String prefixoNomeTabela) {
		List<Object> registro = registros.get(rowIndex);
		return gerarInsert(registro, prefixoNomeTabela);
	}

	public String getInsert(String prefixoNomeTabela) {
		return gerarInsert(null, prefixoNomeTabela);
	}

	public int excluir(int rowIndex, String prefixoNomeTabela) {
		List<Object> registro = registros.get(rowIndex);

		if (chaves) {
			try {
				String delete = gerarDelete(registro, prefixoNomeTabela);
				int i = Persistencia.executar(delete, ConexaoProvedor.getConnection(conexao));
				if (Preferencias.isAreaTransTabelaRegistros()) {
					Util.setContentTransfered(delete);
				}
				return i;
			} catch (Exception ex) {
				Util.stackTraceAndMessage("DELETE", ex, null);
				return -1;
			}
		}

		return -1;
	}

	public List<IndiceValor> getValoresChaves(int rowIndex) {
		List<Object> registro = registros.get(rowIndex);
		List<IndiceValor> resp = new ArrayList<>();

		for (Coluna coluna : getChaves()) {
			IndiceValor obj = new IndiceValor(coluna.getIndice(), registro.get(coluna.getIndice()));
			resp.add(obj);
		}

		return resp;
	}

	public Map<String, String> getMapaChaves(int rowIndex) {
		List<Object> registro = registros.get(rowIndex);
		Map<String, String> resp = new HashMap<>();

		for (Coluna coluna : getChaves()) {
			Object valor = registro.get(coluna.getIndice());
			resp.put(coluna.getNome(), valor.toString());
		}

		return resp;
	}

	public void excluirValoresChaves(List<IndiceValor> lista) {
		int indice = getIndice(lista);

		if (indice != -1) {
			registros.remove(indice);
		}
	}

	private int getIndice(List<IndiceValor> valores) {
		for (int i = 0; i < registros.size(); i++) {
			List<Object> registro = registros.get(i);

			if (ehLinhaValida(registro, valores)) {
				return i;
			}
		}

		return -1;
	}

	private boolean ehLinhaValida(List<Object> registro, List<IndiceValor> valores) {
		for (IndiceValor obj : valores) {
			if (!obj.igual(registro)) {
				return false;
			}
		}

		return true;
	}

	private void getDado(Coluna[] colunas, Object[] valores, StringBuilder sb) {
		Coluna coluna = colunas[0];
		sb.append(Constantes.QL + coluna.getNome() + " = " + coluna.get(valores[0]));

		for (int i = 1; i < colunas.length; i++) {
			coluna = colunas[i];
			sb.append(Constantes.QL + coluna.getNome() + " = " + coluna.get(valores[i]));
		}
	}

	private String gerarUpdate(List<Object> registro, Coluna[] colunas, Object[] valores, String prefixoNomeTabela) {
		StringBuilder resposta = new StringBuilder("UPDATE " + prefixarEsquema(conexao, prefixoNomeTabela, tabela) + " SET ");

		Coluna coluna = colunas[0];
		resposta.append(Constantes.QL + "  " + coluna.getNome() + " = " + coluna.get(valores[0]));

		for (int i = 1; i < colunas.length; i++) {
			coluna = colunas[i];

			if (coluna.isColunaInfo()) {
				continue;
			}

			resposta.append(Constantes.QL + ", " + coluna.getNome() + " = " + coluna.get(valores[i]));
		}

		resposta.append(montarWhere(registro));

		return resposta.toString();
	}

	private String gerarInsert(List<Object> registro, String prefixoNomeTabela) {
		if (colunas.isEmpty()) {
			return null;
		}

		StringBuilder resposta = new StringBuilder("INSERT INTO " + prefixarEsquema(conexao, prefixoNomeTabela, tabela) + " (" + Constantes.QL);

		StringBuilder campo = new StringBuilder();
		StringBuilder valor = new StringBuilder("VALUES (" + Constantes.QL);

		Coluna coluna = colunas.get(0);
		appendCampoValor(Constantes.VAZIO, campo, valor, coluna, registro, prefixoNomeTabela);

		for (int i = 1; i < colunas.size(); i++) {
			coluna = colunas.get(i);

			if (coluna.isColunaInfo()) {
				continue;
			}

			appendCampoValor(", ", campo, valor, coluna, registro, prefixoNomeTabela);
		}

		campo.append(")" + Constantes.QL);
		valor.append(")" + Constantes.QL);

		return resposta.append(campo).append(valor).toString();
	}

	private void appendCampoValor(String string, StringBuilder campo, StringBuilder valor, Coluna coluna, List<Object> registro, String prefixoNomeTabela) {
		campo.append(Constantes.TAB + string + coluna.getNome() + Constantes.QL);

		if (Util.estaVazio(coluna.getSequencia())) {
			if (registro != null) {
				Object valoR = registro.get(coluna.getIndice());
				valor.append(Constantes.TAB + string + coluna.get(valoR) + Constantes.QL);
			} else {
				valor.append(Constantes.TAB + string + coluna.get(coluna.getNome()) + Constantes.QL);
			}
		} else {
			valor.append(Constantes.TAB + string + prefixarEsquema(conexao, prefixoNomeTabela, coluna.getSequencia()) + Constantes.QL);
		}
	}

	private String gerarDelete(List<Object> registro, String prefixoNomeTabela) {
		StringBuilder resposta = new StringBuilder("DELETE FROM " + prefixarEsquema(conexao, prefixoNomeTabela, tabela));
		resposta.append(montarWhere(registro));
		return resposta.toString();
	}

	private String montarWhere(List<Object> registro) {
		List<Coluna> colunasChave = getChaves();

		if(colunasChave.isEmpty()) {
			throw new IllegalStateException("Sem colunas chaves.");
		}

		StringBuilder resposta = new StringBuilder(Constantes.QL + " WHERE ");
		Coluna coluna = colunasChave.get(0);

		if (registro != null) {
			Object valor = registro.get(coluna.getIndice());
			resposta.append(coluna.getNome() + " = " + coluna.get(valor));
		} else {
			resposta.append(coluna.getNome() + " = " + coluna.get(coluna.getNome()));
		}

		for (int i = 1; i < colunasChave.size(); i++) {
			coluna = colunasChave.get(i);

			if (registro != null) {
				Object valor = registro.get(coluna.getIndice());
				resposta.append(Constantes.QL + " AND " + coluna.getNome() + " = " + coluna.get(valor));
			} else {
				resposta.append(Constantes.QL + " AND " + coluna.getNome() + " = " + coluna.get(coluna.getNome()));
			}
		}

		return resposta.toString();
	}

	public static String prefixarEsquema(Conexao conexao, String prefixoNomeTabela, String tabela) {
		String esquema = conexao == null ? Constantes.VAZIO : conexao.getEsquema();
		String prefixoTabela = prefixoNomeTabela == null ? Constantes.VAZIO : prefixoNomeTabela;
		return (Util.estaVazio(esquema) ? Constantes.VAZIO : esquema + ".") + prefixoTabela + tabela;
	}

	private List<Coluna> getChaves() {
		return colunas.stream().filter(Coluna::isChave).collect(Collectors.toList());
	}

	private List<Coluna> getNaoChaves() {
		return colunas.stream().filter(Coluna::isNaoChave).collect(Collectors.toList());
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