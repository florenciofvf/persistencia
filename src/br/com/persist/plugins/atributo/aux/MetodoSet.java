package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;
import br.com.persist.assistencia.Util;

public class MetodoSet extends Container {
	private final Tipo tipo;

	public MetodoSet(Tipo tipo) {
		this.tipo = tipo;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("public void set" + Util.capitalize(tipo.nome) + "(" + tipo.toString() + ") {").ql();
		new Atribuir("this.", tipo.nome, tipo.nome).gerar(tab + 1, pool);
		pool.tab(tab).append("}").ql();
	}
}