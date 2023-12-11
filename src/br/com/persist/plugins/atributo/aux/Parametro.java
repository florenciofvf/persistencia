package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Parametro extends Container {
	private final Tipo tipo;

	public Parametro(Tipo tipo) {
		this.tipo = tipo;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		tipo.gerar(0, pool);
	}
}