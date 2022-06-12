package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;

public abstract class TipoAtomico extends Sentenca {
	public abstract String getValorString();

	@Override
	public void preParametro() throws ChecagemException {
		throw new ChecagemException("Nao suporta parametros >>> " + getClass().getName());
	}

	@Override
	public void encerrar() throws ChecagemException {
		throw new ChecagemException("Nao eh uma funcao >>> " + getClass().getName());
	}
}