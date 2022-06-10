package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;

public class TipoLong extends TipoAtomico {
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