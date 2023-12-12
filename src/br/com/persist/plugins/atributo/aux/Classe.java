package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Classe extends ContainerString {
	public Classe(String nome) {
		super(nome);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append("public class " + string + " {").ql();
		super.gerar(1, pool);
		pool.append("}");
	}
}