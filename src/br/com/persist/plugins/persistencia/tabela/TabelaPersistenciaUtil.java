package br.com.persist.plugins.persistencia.tabela;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;

public class TabelaPersistenciaUtil {
	private TabelaPersistenciaUtil() {
	}

	public static int getIndiceColuna(TabelaPersistencia tabelaPersistencia, String nome) {
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		int qtdColunas = modelo.getColumnCount();
		for (int i = 0; i < qtdColunas; i++) {
			String coluna = modelo.getColumnName(i);
			if (coluna.equalsIgnoreCase(nome)) {
				return i;
			}
		}
		return -1;
	}

	public static List<String> getValoresLinha(TabelaPersistencia tabelaPersistencia, int coluna) {
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
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
}