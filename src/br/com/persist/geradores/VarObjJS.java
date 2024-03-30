package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class VarObjJS extends Container {
	private final String nomeObj;

	public VarObjJS(String nomeObj) {
		super("VarObjJS");
		this.nomeObj = nomeObj;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("var " + nomeObj + " = {").ql();
		super.gerar(tab + 1, pool);
		pool.tab(tab).append("};").ql();
	}
}