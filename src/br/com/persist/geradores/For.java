package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class For extends Container {
	private final String condicao;

	protected For(String condicao) {
		super("For");
		this.condicao = condicao;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("for (" + condicao + ") {").ql();
		super.gerar(tab, pool);
		pool.tab(tab).append("}").ql();
	}
}