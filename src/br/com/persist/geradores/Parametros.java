package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Parametros extends Container {
	public Parametros() {
		super("Parametros");
	}

	public Parametros(Objeto o) {
		super("Parametros");
		add(o);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append("(");
		super.gerar(tab, pool);
		pool.append(")");
	}
}