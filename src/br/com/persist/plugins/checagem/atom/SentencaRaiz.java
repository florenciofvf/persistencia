package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class SentencaRaiz extends FuncaoUnaria {
	public Sentenca getSentenca() {
		return param0();
	}

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		throw new ChecagemException("Nao pode ser executado >>> " + getClass().getName());
	}

	@Override
	public void encerrar() throws ChecagemException {
		throw new ChecagemException("Nao pode ser encerrado >>> " + getClass().getName());
	}
}