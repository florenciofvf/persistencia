package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;

public abstract class FuncaoBinaria extends FuncaoUnaria {
	protected boolean modoInsercao = true;

	public Sentenca param1() {
		return parametros.get(1);
	}

	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		if (!modoInsercao) {
			throw new ChecagemException("O parametro nao pode ser adicionado >>> " + getClass().getName());
		}
		if (parametros.size() == 2) {
			throw new ChecagemException("A funcao ja possui 2 parametros >>> " + getClass().getName());
		}
		addParamImpl(sentenca);
		modoInsercao = false;
	}

	@Override
	public void preParametro() throws ChecagemException {
		if (modoInsercao) {
			throw new ChecagemException("Parametro anterior nao adicionado >>> " + getClass().getName());
		}
		if (parametros.size() == 2) {
			throw new ChecagemException("A funcao ja possui 2 parametros >>> " + getClass().getName());
		}
		modoInsercao = true;
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (parametros.size() != 2) {
			throw new UnsupportedOperationException("A funcao exige 2 parametros >>> " + getClass().getName());
		}
	}
}