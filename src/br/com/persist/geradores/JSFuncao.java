package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class JSFuncao extends ContainerJS {
	private final Parametros parametros;
	private final String nome;

	protected JSFuncao(String nome, Parametros parametros) {
		super("JSFuncao");
		this.nome = nome;
		this.parametros = parametros;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("function " + nome);
		parametros.gerar(0, pool);
		pool.append(" {").ql();
		super.gerar(tab, pool);
		pool.tab(tab).append("}").ql();
	}
}