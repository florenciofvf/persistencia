package br.com.persist.plugins.persistencia.tabela;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.persistencia.PersistenciaOrdenacaoModelo;
import br.com.persist.util.Util;

public class TabelaPersistenciaUtil {
	private TabelaPersistenciaUtil() {
	}

	public static int getIndiceColuna(TabelaPersistencia tabelaPersistencia, String nome) {
		PersistenciaOrdenacaoModelo modelo = (PersistenciaOrdenacaoModelo) tabelaPersistencia.getModel();
		int qtdColunas = modelo.getColumnCount();

		for (int i = 0; i < qtdColunas; i++) {
			String coluna = modelo.getColumnName(i);

			if (coluna.equalsIgnoreCase(nome)) {
				return i;
			}
		}

		return -1;
	}

	public static List<String> getValoresLinhaPelaColuna(TabelaPersistencia tabelaPersistencia, int coluna) {
		PersistenciaOrdenacaoModelo modelo = (PersistenciaOrdenacaoModelo) tabelaPersistencia.getModel();
		List<Integer> linhas = Util.getIndicesLinha(tabelaPersistencia);
		List<String> resposta = new ArrayList<>();

		for (int i : linhas) {
			Object obj = modelo.getValueAt(i, coluna);

			if (obj != null && !Util.estaVazio(obj.toString())) {
				resposta.add(obj.toString());
			}
		}

		return resposta;
	}

	// public static void checarColetores(Tabela tabela, int coluna,
	// TabelaBuscaAuto tabelaBuscaAuto) {
	// OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
	// int total = modelo.getRowCount();
	//
	// for (int i = 0; i < total; i++) {
	// Object obj = modelo.getValueAt(i, coluna);
	//
	// if (obj != null && !Util.estaVazio(obj.toString())) {
	// tabelaBuscaAuto.checarColetores(obj.toString());
	// }
	// }
	// }

	// public static void atualizarLinhaColetores(Tabela tabela, int linha, int
	// coluna, GrupoBuscaAuto grupo) {
	// OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
	// List<Object> registro = modelo.getRegistro(linha);
	// String numero = registro.get(coluna).toString();
	// StringBuilder sb = new StringBuilder();
	//
	// for (TabelaBuscaAuto t : grupo.getTabelas()) {
	// Coletor coletor = t.getColetor(numero);
	//
	// if (coletor.getTotal() > 0) {
	// sb.append(t.getNome() + " [" + coletor.getTotal() + "] ");
	// }
	// }
	//
	// if (sb.length() > 0) {
	// sb.delete(sb.length() - 3, sb.length());
	// }
	//
	// registro.set(registro.size() - 1, sb.toString());
	// }
}