package br.com.persist.plugins.checagem.funcao;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Sentenca;

public abstract class FuncaoVaziaOu1Param extends FuncaoVazia {
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
		throw new ChecagemException(getClass(), "Funcao com zero ou 1 parametro");
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (parametros.size() > 1) {
			throw new ChecagemException(getClass(), "A funcao exige no maximo 1 parametro");
		}
		encerrado = true;
	}
}