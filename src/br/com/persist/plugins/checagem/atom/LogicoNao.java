package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Controle;

public class LogicoNao extends Controle {

	@Override
	public Object executar(Contexto ctx) {
		Boolean arq = (Boolean) param0().executar(ctx);
		return !arq;
	}
}