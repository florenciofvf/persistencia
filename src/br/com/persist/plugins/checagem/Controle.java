package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.List;

public abstract class Controle {
	protected final List<Controle> parametros;

	public Controle() {
		parametros = new ArrayList<>();
	}

	public abstract Object executar(Contexto ctx);

	public Controle param0() {
		return parametros.get(0);
	}

	public Controle param1() {
		return parametros.get(1);
	}
}