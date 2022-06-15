package br.com.persist.plugins.checagem.atomico;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.TipoAtomico;

public class TipoDouble extends TipoAtomico {
	private Double valor;

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		return valor;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	@Override
	public String getValorString() {
		return valor.toString();
	}
}