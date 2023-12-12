package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Import extends ContainerString {
	public Import(String nome) {
		super(nome);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("import " + string).append(";").ql();
	}
}