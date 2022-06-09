package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.List;

public abstract class Sentenca {
	protected final List<Sentenca> parametros;

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
}