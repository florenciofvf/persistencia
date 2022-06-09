package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Controle;

public class TipoBoolean extends Controle {
	private Boolean valor;

	@Override
	public Object executar(Contexto ctx) {
		return valor;
	}

	public Boolean getValor() {
		return valor;
	}

	public void setValor(Boolean valor) {
		this.valor = valor;
	}
}