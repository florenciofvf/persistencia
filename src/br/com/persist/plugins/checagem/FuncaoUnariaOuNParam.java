package br.com.persist.plugins.checagem;

public abstract class FuncaoUnariaOuNParam extends FuncaoUnaria {

	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		if (!modoInsercao) {
			throw new ChecagemException(getClass(), "O parametro nao pode ser adicionado");
		}
		addParamImpl(sentenca);
		modoInsercao = false;
	}

	@Override
	public void preParametro() throws ChecagemException {
		if (modoInsercao) {
			throw new ChecagemException(getClass(), "Parametro anterior nao adicionado");
		}
		modoInsercao = true;
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (parametros.isEmpty()) {
			throw new ChecagemException(getClass(), "A funcao exige no minimo 1 parametro");
		}
		encerrado = true;
	}
}