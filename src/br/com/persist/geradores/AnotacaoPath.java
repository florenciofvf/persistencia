package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class AnotacaoPath extends ObjetoString {
	private final boolean ql;

	public AnotacaoPath(String string, boolean ql) {
		super("AnotacaoPath", string);
		this.ql = ql;
	}

	public AnotacaoPath(String string) {
		this(string, false);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("@Path(" + string + ")");
		if (ql) {
			pool.ql();
		}
	}
}