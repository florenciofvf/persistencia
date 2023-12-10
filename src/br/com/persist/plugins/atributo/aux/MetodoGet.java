package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;
import br.com.persist.assistencia.Util;

public class MetodoGet extends Container {
	private final Tipo tipo;

	public MetodoGet(Tipo tipo) {
		this.tipo = tipo;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("public " + tipo.classe + " get" + Util.capitalize(tipo.nome) + "() {").ql();
		new Return("", tipo.nome).gerar(tab, pool);
		pool.tab(tab).append("}").ql();
	}
}