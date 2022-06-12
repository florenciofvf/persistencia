package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;

public abstract class FuncaoUnaria extends TipoFuncao {
	public Sentenca param0() {
		return parametros.get(0);
	}

	@Override
	public void addParam(Sentenca sentenca) {
		if (!parametros.isEmpty()) {
			throw new UnsupportedOperationException("A funcao ja possui 1 parametro >>> " + getClass().getName());
		}
		super.addParam(sentenca);
	}

	@Override
	public void preParametro() {
		throw new UnsupportedOperationException("Suporta apenas 1 parametro >>> " + getClass().getName());
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (parametros.size() != 1) {
			throw new UnsupportedOperationException("A funcao exige 1 parametro >>> " + getClass().getName());
		}
	}
}