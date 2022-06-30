package br.com.persist.plugins.checagem;

public abstract class FuncaoVaziaOu1Param extends FuncaoVazia {

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
		throw new ChecagemException("Funcao com zero ou 1 parametro >>> " + getClass().getName());
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (parametros.size() > 1) {
			throw new ChecagemException("A funcao exige no maximo 1 parametro >>> " + getClass().getName());
		}
		encerrado = true;
	}
}