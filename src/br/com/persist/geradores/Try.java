package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Try extends Container {
	private final Catch catche;

	protected Try(Catch catche) {
		super("Try");
		this.catche = catche;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("try {").ql();
		super.gerar(tab, pool);
		if (catche == null) {
			pool.tab(tab).append("}").ql();
		} else {
			catche.gerar(tab, pool);
		}
	}
}