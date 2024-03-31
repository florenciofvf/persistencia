package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class JSVar extends ObjetoString {
	public JSVar(String string) {
		super("JSVar", string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(string);
	}
}