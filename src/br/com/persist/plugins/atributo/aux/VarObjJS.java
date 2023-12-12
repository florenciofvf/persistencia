package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class VarObjJS extends Container {
	final String nome;

	public VarObjJS(String nome) {
		this.nome = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("var " + nome + " = {").ql();
		super.gerar(tab + 1, pool);
		pool.tab(tab).append("};").ql();
	}
}