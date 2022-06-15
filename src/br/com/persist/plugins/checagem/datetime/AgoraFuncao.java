package br.com.persist.plugins.checagem.datetime;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoVazia;

public class AgoraFuncao extends FuncaoVazia {
	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		return System.currentTimeMillis();
	}
}