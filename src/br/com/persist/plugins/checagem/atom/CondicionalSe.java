package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Sentenca;

public class CondicionalSe extends Sentenca {

	@Override
	public Object executar(Contexto ctx) {
		Boolean pri = (Boolean) param0().executar(ctx);
		Object seg = param1().executar(ctx);
		return pri ? seg : null;
	}
}