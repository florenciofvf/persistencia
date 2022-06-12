package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;

public abstract class FuncaoVazia extends TipoFuncao {
	@Override
	public void addParam(Sentenca sentenca) {
		throw new UnsupportedOperationException("A funcao nao suporta parametros >>> " + getClass().getName());
	}

	@Override
	public void preParametro() {
		throw new UnsupportedOperationException("Nao suporta qualquer parametro >>> " + getClass().getName());
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (!parametros.isEmpty()) {
			throw new UnsupportedOperationException("A funcao nao exige parametros >>> " + getClass().getName());
		}
	}
}