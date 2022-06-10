package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.List;

public abstract class Sentenca {
	protected final List<Sentenca> parametros;
	protected Sentenca pai;

	public Sentenca() {
		parametros = new ArrayList<>();
	}

	public abstract Object executar(Contexto ctx);

	public Sentenca param0() {
		return parametros.get(0);
	}

	public Sentenca param1() {
		return parametros.get(1);
	}

	public void add(Sentenca sentenca) {
		sentenca.pai = this;
		parametros.add(sentenca);
	}

	public Sentenca getPai() {
		return pai;
	}
}