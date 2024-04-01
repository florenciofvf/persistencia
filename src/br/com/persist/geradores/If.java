package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class If extends Container {
	private final String condicao;
	private final Else elsee;

	protected If(String condicao, Else elsee) {
		super("If");
		this.condicao = condicao;
		this.elsee = elsee;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("if (" + condicao + ") {").ql();
		super.gerar(tab, pool);
		if (elsee == null) {
			pool.tab(tab).append("}").ql();
		} else {
			elsee.gerar(tab, pool);
		}
	}

	public ElseIf criarElseIf(String condicao) {
		ElseIf elseIf = new ElseIf(condicao);
		add(elseIf);
		return elseIf;
	}
}