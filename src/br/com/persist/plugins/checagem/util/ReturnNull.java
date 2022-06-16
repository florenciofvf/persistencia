package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnaria;

public class ReturnNull extends FuncaoUnaria {
	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		param0().executar(ctx);
		return null;
	}
}