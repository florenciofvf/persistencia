package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Anotacao extends ObjetoString {
	private final boolean ql;

	public Anotacao(String string, boolean ql) {
		super("Anotacao", string);
		this.ql = ql;
	}

	public Anotacao(String string) {
		this(string, false);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("@" + string);
		if (ql) {
			pool.ql();
		}
	}
}