package br.com.persist.plugins.checagem;

public abstract class FuncaoUnariaOuNParam extends FuncaoUnaria {

	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		if (!modoInsercao) {
			throw new ChecagemException("O parametro nao pode ser adicionado >>> " + getClass().getName());
		}
		addParamImpl(sentenca);
		modoInsercao = false;
	}

	@Override
	public void preParametro() throws ChecagemException {
		if (modoInsercao) {
			throw new ChecagemException("Parametro anterior nao adicionado >>> " + getClass().getName());
		}
		modoInsercao = true;
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (parametros.isEmpty()) {
			throw new ChecagemException("A funcao exige no minimo 1 parametro >>> " + getClass().getName());
		}
		encerrado = true;
	}
}