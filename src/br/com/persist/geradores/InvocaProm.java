package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class InvocaProm extends ContainerJS {
	private final String string;

	protected InvocaProm(String string) {
		super("InvocaProm");
		this.string = string;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(string).ql();
		super.gerar(tab, pool);
		pool.tab(tab).append("});").ql();
	}
}