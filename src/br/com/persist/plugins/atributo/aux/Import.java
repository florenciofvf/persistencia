package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Import extends Container {
	private final String nome;

	public Import(String nome) {
		this.nome = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("import " + nome).append(";").ql();
	}
}