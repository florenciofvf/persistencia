package br.com.persist.modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.Persistencia;
import br.com.persist.tabela.Coluna;
import br.com.persist.tabela.IndiceValor;
import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public class RegistroModelo implements TableModel {
	private final List<List<Object>> registros;
	private final List<Coluna> colunas;
	private final boolean chaves;
	private final String tabela;
	private Conexao conexao;

	public RegistroModelo(List<Coluna> colunas, List<List<Object>> registros, String tabela) {
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
				String update = gerarUpdate(registro, new Coluna[] { coluna }, new Object[] { aValue });
				Persistencia.executar(update, Conexao.getConnection(conexao));
				registro.set(columnIndex, aValue);
				if (Constantes.area_trans_tabela_registros) {
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

		getDado(registro, colunas.toArray(new Coluna[0]), valores.toArray(new Object[0]), sb);
	}

	public String getUpdate(int rowIndex) {
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

			return gerarUpdate(registro, naoChaves.toArray(new Coluna[0]), valores.toArray(new Object[0]));
		}

		return null;
	}

	public int excluir(int rowIndex) {
		List<Object> registro = registros.get(rowIndex);

		if (chaves) {
			try {
				String delete = gerarDelete(registro);
				int i = Persistencia.executar(delete, Conexao.getConnection(conexao));
				if (Constantes.area_trans_tabela_registros) {
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
		List<Coluna> chaves = getChaves();

		for (Coluna coluna : chaves) {
			IndiceValor obj = new IndiceValor(registro.get(coluna.getIndice()), coluna.getIndice());
			resp.add(obj);
		}

		return resp;
	}

	public Map<String, String> getMapaChaves(int rowIndex) {
		List<Object> registro = registros.get(rowIndex);
		Map<String, String> resp = new HashMap<>();
		List<Coluna> chaves = getChaves();

		for (Coluna coluna : chaves) {
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

	private void getDado(List<Object> registro, Coluna[] colunas, Object[] valores, StringBuilder sb) {
		Coluna coluna = colunas[0];
		sb.append(Constantes.QL + coluna.getNome() + " = " + coluna.get(valores[0]));

		for (int i = 1; i < colunas.length; i++) {
			coluna = colunas[i];
			sb.append(Constantes.QL + coluna.getNome() + " = " + coluna.get(valores[i]));
		}
	}

	private String gerarUpdate(List<Object> registro, Coluna[] colunas, Object[] valores) {
		StringBuilder builder = new StringBuilder("UPDATE " + tabela + " SET ");

		Coluna coluna = colunas[0];
		builder.append(Constantes.QL + "  " + coluna.getNome() + " = " + coluna.get(valores[0]));

		for (int i = 1; i < colunas.length; i++) {
			coluna = colunas[i];
			builder.append(Constantes.QL + ", " + coluna.getNome() + " = " + coluna.get(valores[i]));
		}

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

		StringBuilder builder = new StringBuilder(Constantes.QL + " WHERE ");
		builder.append(coluna.getNome() + " = " + coluna.get(registro.get(coluna.getIndice())));

		for (int i = 1; i < lista.size(); i++) {
			coluna = lista.get(i);
			builder.append(
					Constantes.QL + " AND " + coluna.getNome() + " = " + coluna.get(registro.get(coluna.getIndice())));
		}

		return builder.toString();
	}

	private List<Coluna> getChaves() {
		return colunas.stream().filter(Coluna::isChave).collect(Collectors.toList());
	}

	private List<Coluna> getNaoChaves() {
		return colunas.stream().filter(Coluna::isNaoChave).collect(Collectors.toList());
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}