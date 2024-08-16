package br.com.persist.plugins.objeto.vinculo;

import java.util.List;

import br.com.persist.plugins.objeto.ObjetoException;

public class PesquisaUtil {
	private PesquisaUtil() {
	}

	public static boolean contem(Pesquisa pesquisa, List<Pesquisa> pesquisas) throws ObjetoException {
		if (pesquisa == null || pesquisas == null) {
			throw new ObjetoException("boolean contem(): pesquisa == null || pesquisas == null");
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