package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class NewLine extends Objeto {
	public NewLine() {
		super("NewLine");
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.ql();
	}
}