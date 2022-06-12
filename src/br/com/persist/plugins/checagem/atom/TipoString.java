package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;

public class TipoString extends TipoAtomico {
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

	@Override
	public String getValorString() {
		return getValor();
	}
}