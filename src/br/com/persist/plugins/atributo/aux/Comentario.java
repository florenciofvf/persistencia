package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Comentario extends Container {
	private final String string;

	public Comentario(String string) {
		this.string = string;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("//" + string).append(";").ql();
	}
}