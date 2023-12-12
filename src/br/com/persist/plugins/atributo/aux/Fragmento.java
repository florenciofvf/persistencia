package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Fragmento extends Container {
	final String string;

	public Fragmento(String nome) {
		this.string = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(string);
	}

	@Override
	public String toString() {
		return string;
	}
}