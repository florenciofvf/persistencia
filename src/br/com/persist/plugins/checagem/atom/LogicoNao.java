package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;

public class LogicoNao extends FuncaoUnaria {
	@Override
	public Object executar(Contexto ctx) {
		Boolean pri = (Boolean) param0().executar(ctx);
		return !pri;
	}
}