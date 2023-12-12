package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Parametros extends Container {
	public Parametros() {
		this(null);
	}

	public Parametros(Container c) {
		add(c);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append("(");
		super.gerar(tab, pool);
		pool.append(")");
	}
}