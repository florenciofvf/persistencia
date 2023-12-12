package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Sequencia extends Fragmento {
	public Sequencia(String string) {
		super(string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append(string);
	}
}