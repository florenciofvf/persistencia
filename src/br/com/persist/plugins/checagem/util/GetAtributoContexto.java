package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.atomico.TipoString;

public class GetAtributoContexto extends TipoString {
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