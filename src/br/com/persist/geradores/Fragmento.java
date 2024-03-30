package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Fragmento extends ObjetoString {
	public Fragmento(String string) {
		super("Fragmento", string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(string);
	}
}