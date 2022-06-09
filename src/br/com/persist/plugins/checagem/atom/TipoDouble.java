package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Sentenca;

public class TipoDouble extends Sentenca {
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