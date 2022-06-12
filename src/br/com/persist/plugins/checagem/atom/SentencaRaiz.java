package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;

public class SentencaRaiz extends FuncaoUnaria {
	public Sentenca getSentenca() {
		return param0();
	}

	@Override
	public Object executar(Contexto ctx) {
		throw new UnsupportedOperationException("Nao pode ser executado >>> " + getClass().getName());
	}
}