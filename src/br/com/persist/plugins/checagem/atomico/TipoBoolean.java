package br.com.persist.plugins.checagem.atomico;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.TipoAtomico;

public class TipoBoolean extends TipoAtomico {
	private Boolean valor;

	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		return valor;
	}

	public Boolean getValor() {
		return valor;
	}

	public void setValor(Boolean valor) {
		this.valor = valor;
	}

	@Override
	public String getValorString() {
		return valor.toString();
	}
}