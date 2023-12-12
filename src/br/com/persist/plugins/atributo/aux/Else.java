package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Else extends Container {
	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("} else {").ql();
		super.gerar(tab + 1, pool);
		pool.tab(tab).append("}").ql();
	}
}