package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnaria;

public class Classe extends FuncaoUnaria {
	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(ctx);
		if (pri != null) {
			return pri.getClass().getName();
		}
		return "null";
	}
}