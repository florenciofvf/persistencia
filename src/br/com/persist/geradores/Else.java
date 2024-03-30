package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Else extends Container {
	public Else() {
		super("Else");
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("} else {").ql();
		super.gerar(tab + 1, pool);
		pool.tab(tab).append("}").ql();
	}
}