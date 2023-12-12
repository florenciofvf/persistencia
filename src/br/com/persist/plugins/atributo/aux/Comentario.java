package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Comentario extends ContainerString {
	public Comentario(String string) {
		super(string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("//" + string).ql();
	}
}