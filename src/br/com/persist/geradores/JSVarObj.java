package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class JSVarObj extends ContainerJS {
	private final String nomeObj;

	protected JSVarObj(String nomeObj) {
		super("JSVarObj");
		this.nomeObj = nomeObj;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("var " + nomeObj + " = {").ql();
		super.gerar(tab, pool);
		pool.tab(tab).append("};").ql();
	}
}