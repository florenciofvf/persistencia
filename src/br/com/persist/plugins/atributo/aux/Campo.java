package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Campo extends Container {
	private final Tipo tipo;

	public Campo(Tipo tipo) {
		this.tipo = tipo;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("private ");
		tipo.gerar(0, pool);
		pool.append(";").ql();
	}
}