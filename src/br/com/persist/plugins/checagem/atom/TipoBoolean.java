package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;

public class TipoBoolean extends TipoAtomico {
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