package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Espaco extends Objeto {
	protected Espaco() {
		super("Espaco");
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append(" ");
	}
}