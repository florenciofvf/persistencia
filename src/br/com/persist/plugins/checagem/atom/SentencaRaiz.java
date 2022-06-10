package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Sentenca;

public class SentencaRaiz extends Sentenca {
	@Override
	public void addParam(Sentenca sentenca) {
		if (parametros.isEmpty()) {
			super.addParam(sentenca);
		} else {
			throw new IllegalStateException("SentencaRaiz size > 1");
		}
	}

	@Override
	public Object executar(Contexto ctx) {
		throw new UnsupportedOperationException("Not supported.");
	}
}