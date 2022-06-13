package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public abstract class Sentenca {
	protected Sentenca pai;

	public abstract Object executar(Contexto ctx) throws ChecagemException;

	public Sentenca getPai() {
		return pai;
	}
}