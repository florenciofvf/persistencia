package br.com.persist.plugins.checagem.atomico;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.TipoAtomico;

public class TipoLong extends TipoAtomico {
	private Long valor;

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		return valor;
	}

	public Long getValor() {
		return valor;
	}

	public void setValor(Long valor) {
		this.valor = valor;
	}

	@Override
	public String getValorString() {
		return valor.toString();
	}
}