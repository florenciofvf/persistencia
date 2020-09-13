package br.com.persist.plugins.objeto.internal;

import java.util.List;

import br.com.persist.plugins.objeto.auto.BuscaAutoColetor;
import br.com.persist.plugins.objeto.auto.GrupoBuscaAuto;
import br.com.persist.plugins.objeto.auto.TabelaBuscaAuto;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.plugins.persistencia.tabela.TabelaPersistencia;
import br.com.persist.util.Util;

public class InternalUtil {
	private InternalUtil() {
	}

	public static void checarColetores(TabelaPersistencia tabela, int coluna, TabelaBuscaAuto tabelaBuscaAuto) {
		OrdenacaoModelo modelo = tabela.getModelo();
		int total = modelo.getRowCount();

		for (int i = 0; i < total; i++) {
			Object obj = modelo.getValueAt(i, coluna);

			if (obj != null && !Util.estaVazio(obj.toString())) {
				tabelaBuscaAuto.checarColetores(obj.toString());
			}
		}
	}

	public static void atualizarLinhaColetores(TabelaPersistencia tabela, int linha, int coluna, GrupoBuscaAuto grupo) {
		OrdenacaoModelo modelo = tabela.getModelo();
		List<Object> registro = modelo.getRegistro(linha);
		String numero = registro.get(coluna).toString();
		StringBuilder sb = new StringBuilder();

		for (TabelaBuscaAuto t : grupo.getTabelas()) {
			BuscaAutoColetor coletor = t.getColetor(numero);

			if (coletor.getTotal() > 0) {
				sb.append(t.getNome() + " [" + coletor.getTotal() + "] ");
			}
		}

		if (sb.length() > 0) {
			sb.delete(sb.length() - 3, sb.length());
		}

		registro.set(registro.size() - 1, sb.toString());
	}
}