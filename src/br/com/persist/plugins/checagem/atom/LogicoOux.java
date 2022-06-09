package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Controle;

public class LogicoOux extends Controle {

	@Override
	public Object executar(Contexto ctx) {
		Boolean pri = (Boolean) param0().executar(ctx);
		Boolean seg = (Boolean) param1().executar(ctx);
		return pri ^ seg;
	}
}