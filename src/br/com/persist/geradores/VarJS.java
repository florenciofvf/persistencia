package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class VarJS extends ObjetoString {
	public VarJS(String string) {
		super("VarJS", string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(string);
	}
}