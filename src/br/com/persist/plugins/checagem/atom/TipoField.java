package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;

public class TipoField extends TipoString {
	private String valorNormal;

	@Override
	public Object executar(Contexto ctx) {
		return ctx.get(valorNormal);
	}

	@Override
	public void setValor(String valor) {
		valorNormal = valor.substring(1);
		super.setValor(valor);
	}
}