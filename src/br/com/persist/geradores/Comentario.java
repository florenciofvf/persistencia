package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Comentario extends ObjetoString {
	protected Comentario(String string) {
		super("Comentario", string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("//" + string).ql();
	}
}