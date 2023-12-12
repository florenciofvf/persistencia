package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Fragmento extends ContainerString {
	public Fragmento(String string) {
		super(string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(string);
	}
}