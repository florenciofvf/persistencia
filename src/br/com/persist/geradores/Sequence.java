package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Sequence extends ObjetoString {
	protected Sequence(String string) {
		super("Sequence", string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append(string);
	}
}