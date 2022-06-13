package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;

public abstract class FuncaoTernaria extends FuncaoBinaria {

	public Sentenca param2() {
		return parametros.get(2);
	}

	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		if (!modoInsercao) {
			throw new ChecagemException("O parametro nao pode ser adicionado >>> " + getClass().getName());
		}
		if (parametros.size() == 3) {
			throw new ChecagemException("A funcao ja possui 3 parametros >>> " + getClass().getName());
		}
		addParamImpl(sentenca);
		modoInsercao = false;
	}

	@Override
	public void preParametro() throws ChecagemException {
		if (modoInsercao) {
			throw new ChecagemException("Parametro anterior nao adicionado >>> " + getClass().getName());
		}
		if (parametros.size() == 3) {
			throw new ChecagemException("A funcao ja possui 3 parametros >>> " + getClass().getName());
		}
		modoInsercao = true;
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (parametros.size() != 3) {
			throw new ChecagemException("A funcao exige 3 parametros >>> " + getClass().getName());
		}
		encerrado = true;
	}
}