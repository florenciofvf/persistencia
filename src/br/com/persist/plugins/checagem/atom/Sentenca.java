package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public abstract class Sentenca {
	protected Sentenca pai;

	public abstract void preParametro() throws ChecagemException;

	public abstract void encerrar() throws ChecagemException;

	public abstract Object executar(Contexto ctx);

	public Sentenca getPai() {
		return pai;
	}
}