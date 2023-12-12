package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class VarObjJS extends ContainerString {
	public VarObjJS(String nome) {
		super(nome);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("var " + string + " = {").ql();
		super.gerar(tab + 1, pool);
		pool.tab(tab).append("};").ql();
	}
}