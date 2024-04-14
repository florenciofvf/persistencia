package br.com.persist.plugins.objeto.vinculo;

import java.util.List;

public class PesquisaUtil {
	private PesquisaUtil() {
	}

	public static boolean contem(Pesquisa pesquisa, List<Pesquisa> pesquisas) {
		if (pesquisa == null || pesquisas == null) {
			throw new IllegalStateException();
		}
		for (Pesquisa pesq : pesquisas) {
			if (pesq.getNome().equalsIgnoreCase(pesquisa.getNome())
					&& pesq.getReferencia().igual(pesquisa.getReferencia())) {
				return true;
			}
		}
		return false;
	}
}