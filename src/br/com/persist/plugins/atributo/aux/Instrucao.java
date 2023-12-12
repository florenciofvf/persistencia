package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Instrucao extends ContainerString {
	public Instrucao(String string) {
		super(string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(string).append(";").ql();
	}
}