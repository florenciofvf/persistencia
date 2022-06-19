package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoVazia;

public class Null extends FuncaoVazia {
	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		return null;
	}
}