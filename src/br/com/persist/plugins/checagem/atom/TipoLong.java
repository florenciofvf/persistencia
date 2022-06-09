package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Controle;

public class TipoLong extends Controle {
	private Long valor;

	@Override
	public Object executar(Contexto ctx) {
		return valor;
	}

	public Long getValor() {
		return valor;
	}

	public void setValor(Long valor) {
		this.valor = valor;
	}
}