package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Sentenca;

public class TipoString extends Sentenca {
	private String valor;

	@Override
	public Object executar(Contexto ctx) {
		return valor;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}