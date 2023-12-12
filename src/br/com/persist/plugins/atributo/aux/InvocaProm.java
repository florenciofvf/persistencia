package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class InvocaProm extends Container {
	private final String string;

	public InvocaProm(String nome) {
		this.string = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(string);
		for (Container c : lista) {
			c.gerar(tab + 1, pool);
		}
		pool.tab(tab).append("});").ql();
	}
}