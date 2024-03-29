package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class InvocaProm extends ContainerString {
	public InvocaProm(String string) {
		super(string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(string).ql();
		super.gerar(tab + 1, pool);
		pool.tab(tab).append("});").ql();
	}
}