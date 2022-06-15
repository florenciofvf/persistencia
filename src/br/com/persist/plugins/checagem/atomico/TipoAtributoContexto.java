package br.com.persist.plugins.checagem.atomico;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class TipoAtributoContexto extends TipoString {
	private String valorNormal;

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		return ctx.get(valorNormal);
	}

	@Override
	public void setValor(String valor) {
		valorNormal = valor.substring(1);
		super.setValor(valor);
	}
}