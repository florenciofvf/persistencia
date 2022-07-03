package br.com.persist.plugins.checagem;

public abstract class FuncaoBinariaOuNParam extends FuncaoUnaria {

	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		if (!modoInsercao) {
			throw new ChecagemException(getClass(), "O parametro nao pode ser adicionado");
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
			throw new ChecagemException(getClass(), "Parametro anterior nao adicionado");
		}
		modoInsercao = true;
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (parametros.size() < 2) {
			throw new ChecagemException(getClass(), "A funcao exige no minimo 2 parametros");
		}
		encerrado = true;
	}
}