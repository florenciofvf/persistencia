package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Espaco extends Container {
	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append(" ");
	}
}