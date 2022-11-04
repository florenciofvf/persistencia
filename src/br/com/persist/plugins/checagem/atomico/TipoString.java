package br.com.persist.plugins.checagem.atomico;

import java.util.Objects;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.TipoAtomico;

public class TipoString implements TipoAtomico {
	private final String valor;

	public TipoString(String valor) {
		this.valor = Objects.requireNonNull(valor);
	}

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		return valor;
	}

	public String getValor() {
		return valor;
	}

	@Override
	public String toString() {
		return valor;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "Texto >>> " + toString();
	}
}