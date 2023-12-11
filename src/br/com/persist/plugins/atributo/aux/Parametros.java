package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Parametros extends Container {
	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append("(");
		super.gerar(tab, pool);
		pool.append(")");
	}
}