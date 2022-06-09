package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Sentenca;

public class LogicoOux extends Sentenca {

	@Override
	public Object executar(Contexto ctx) {
		Boolean pri = (Boolean) param0().executar(ctx);
		Boolean seg = (Boolean) param1().executar(ctx);
		return pri ^ seg;
	}
}