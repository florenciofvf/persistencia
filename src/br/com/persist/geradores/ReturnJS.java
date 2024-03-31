package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class ReturnJS extends ContainerJS {
	protected ReturnJS() {
		super("ReturnJS");
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("return {").ql();
		super.gerar(tab, pool);
		pool.tab(tab).append("};").ql();
	}
}