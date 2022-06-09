package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Controle;

public class LogicoOu extends Controle {

	@Override
	public Object executar(Contexto ctx) {
		Boolean arg0 = (Boolean) param0().executar(ctx);
		Boolean arq1 = (Boolean) param1().executar(ctx);
		return arg0 || arq1;
	}
}