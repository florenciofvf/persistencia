package br.com.persist.plugins.checagem.funcao;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Sentenca;
import br.com.persist.plugins.checagem.TipoFuncao;

public abstract class FuncaoVazia extends TipoFuncao {
	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		throw new ChecagemException(getClass(), "A funcao nao suporta parametros");
	}

	@Override
	public void preParametro() throws ChecagemException {
		throw new ChecagemException(getClass(), "Nao suporta qualquer parametro");
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (!parametros.isEmpty()) {
			throw new ChecagemException(getClass(), "A funcao nao exige parametros");
		}
		encerrado = true;
	}
}