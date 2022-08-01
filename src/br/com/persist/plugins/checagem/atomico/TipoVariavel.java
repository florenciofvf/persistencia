package br.com.persist.plugins.checagem.atomico;

import java.util.Objects;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.TipoAtomico;

public class TipoVariavel extends TipoAtomico {
	private final String valor;

	public TipoVariavel(String valor) {
		this.valor = Objects.requireNonNull(valor).substring(1);
	}

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		return ctx.get(valor);
	}

	public String getValor() {
		return valor;
	}

	@Override
	public String toString() {
		return valor;
	}
}