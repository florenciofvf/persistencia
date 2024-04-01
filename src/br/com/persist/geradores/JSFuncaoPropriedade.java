package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class JSFuncaoPropriedade extends ContainerJS {
	private final Parametros parametros;
	private final boolean separar;
	private final String nome;

	protected JSFuncaoPropriedade(boolean separar, String nome, Parametros parametros) {
		super("JSFuncaoPropriedade");
		this.nome = nome;
		this.separar = separar;
		this.parametros = parametros;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(nome + ": function");
		parametros.gerar(0, pool);
		pool.append(" {").ql();
		super.gerar(tab, pool);
		pool.tab(tab).append("}");
		if (separar) {
			pool.append(",");
		}
		pool.ql();
	}
}