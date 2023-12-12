package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class ReturnJS extends Container {
	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("return {").ql();
		super.gerar(tab + 1, pool);
		pool.tab(tab).append("};").ql();
	}
}