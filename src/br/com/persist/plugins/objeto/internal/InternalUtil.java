package br.com.persist.plugins.objeto.internal;

import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.objeto.vinculo.Coletor;
import br.com.persist.plugins.objeto.vinculo.Grupo;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.plugins.persistencia.tabela.TabelaPersistencia;

public class InternalUtil {
	private InternalUtil() {
	}

	public static void atualizarColetores(TabelaPersistencia tabela, int coluna, Referencia referencia) {
		OrdenacaoModelo modelo = tabela.getModelo();
		int total = modelo.getRowCount();
		for (int i = 0; i < total; i++) {
			Object obj = modelo.getValueAt(i, coluna);
			if (obj != null && !Util.estaVazio(obj.toString())) {
				referencia.atualizarColetores(obj.toString());
			}
		}
	}

	public static void consolidarColetores(TabelaPersistencia tabela, int linha, int coluna, Grupo grupo) {
		List<Object> registro = tabela.getModelo().getRegistro(linha);
		String valor = registro.get(coluna).toString();
		StringBuilder builder = new StringBuilder();
		final String espaco = "   ";
		for (Referencia ref : grupo.getReferencias()) {
			Coletor coletor = ref.getColetor(valor);
			if (coletor.getTotal() > 0) {
				builder.append(ref.getTabela() + " [" + coletor.getTotal() + "]" + espaco);
			}
		}
		if (builder.length() > 0) {
			builder.delete(builder.length() - espaco.length(), builder.length());
		}
		registro.set(registro.size() - 1, builder.toString());
	}
}