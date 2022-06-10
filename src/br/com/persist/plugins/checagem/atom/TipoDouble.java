package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;

public class TipoDouble extends TipoAtomico {
	private Double valor;

	@Override
	public Object executar(Contexto ctx) {
		return valor;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
}