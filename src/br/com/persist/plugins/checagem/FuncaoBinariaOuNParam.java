package br.com.persist.plugins.checagem;

public abstract class FuncaoBinariaOuNParam extends FuncaoUnaria {
	protected boolean modoInsercao = true;

	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		if (!modoInsercao) {
			throw new ChecagemException("O parametro nao pode ser adicionado >>> " + getClass().getName());
		}
		addParamImpl(sentenca);
		modoInsercao = false;
	}

	public Sentenca param1() {
		return parametros.get(1);
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
		if (parametros.size() < 2) {
			throw new ChecagemException("A funcao exige no minimo 2 parametros >>> " + getClass().getName());
		}
		encerrado = true;
	}
}