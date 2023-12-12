package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class FuncaoJS extends Container {
	private final String nome;
	private final Parametros parametros;

	public FuncaoJS(String nome, Parametros parametros) {
		this.nome = nome;
		this.parametros = parametros;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(nome);
		parametros.gerar(0, pool);
		pool.append(" {").ql();
		for (Container c : lista) {
			c.gerar(tab + 1, pool);
		}
		pool.tab(tab).append("}");
		if (!nome.startsWith("function")) {
			pool.append(";");
		}
		pool.ql();
	}
}