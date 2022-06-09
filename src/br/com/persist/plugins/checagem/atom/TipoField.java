package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;

public class TipoField extends TipoString {
	@Override
	public Object executar(Contexto ctx) {
		return ctx.get(getValor());
	}
}