package br.com.persist.plugins.checagem.funcao;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Sentenca;
import br.com.persist.plugins.checagem.TipoFuncao;

public abstract class FuncaoUnaria extends TipoFuncao {
	public Sentenca param0() {
		return parametros.get(0);
	}

	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		if (!parametros.isEmpty()) {
			throw new ChecagemException(getClass(), "A funcao ja possui 1 parametro");
		}
		addParamImpl(sentenca);
		modoInsercao = false;
	}

	@Override
	public void preParametro() throws ChecagemException {
		throw new ChecagemException(getClass(), "Suporta apenas 1 parametro");
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (parametros.size() != 1) {
			throw new ChecagemException(getClass(), "A funcao exige 1 parametro");
		}
		encerrado = true;
	}
}