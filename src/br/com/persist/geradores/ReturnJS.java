package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class ReturnJS extends Container {
	public ReturnJS() {
		super("ReturnJS");
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("return {").ql();
		super.gerar(tab + 1, pool);
		pool.tab(tab).append("};").ql();
	}
}