package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;

public class IsEmptyFuncao extends FuncaoUnaria {
	@Override
	public Object executar(Contexto ctx) {
		Object pri = param0().executar(ctx);
		return pri == null || pri.toString().trim().length() == 0;
	}
}