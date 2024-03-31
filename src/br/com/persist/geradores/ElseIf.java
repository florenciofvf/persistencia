package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class ElseIf extends Container {
	private final String condicao;

	public ElseIf(String condicao) {
		super("ElseIf");
		this.condicao = condicao;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("} else if (" + condicao + ") {").ql();
		super.gerar(tab, pool);
	}
}