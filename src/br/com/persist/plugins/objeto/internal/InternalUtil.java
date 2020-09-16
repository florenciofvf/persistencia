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

	public static void atualizarColetores(TabelaPersistencia tabela, int coluna, TabelaBuscaAuto tabelaBuscaAuto) {
		OrdenacaoModelo modelo = tabela.getModelo();
		int total = modelo.getRowCount();

		for (int i = 0; i < total; i++) {
			Object obj = modelo.getValueAt(i, coluna);

			if (obj != null && !Util.estaVazio(obj.toString())) {
				tabelaBuscaAuto.atualizarColetores(obj.toString());
			}
		}
	}

	public static void consolidarNoRegistroUsandoColetores(TabelaPersistencia tabela, int linha, int coluna,
			GrupoBuscaAuto grupo) {
		List<Object> registro = tabela.getModelo().getRegistro(linha);
		String valor = registro.get(coluna).toString();
		StringBuilder builder = new StringBuilder();

		for (TabelaBuscaAuto t : grupo.getTabelas()) {
			BuscaAutoColetor coletor = t.getColetor(valor);

			if (coletor.getTotal() > 0) {
				builder.append(t.getNome() + " [" + coletor.getTotal() + "] ");
			}
		}

		if (builder.length() > 0) {
			builder.delete(builder.length() - 1, builder.length());
		}

		registro.set(registro.size() - 1, builder.toString());
	}
}