package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public abstract class Funcao extends Container {
	private final String modificadores;
	private final String retorno;
	private final String nome;
	private final Parametros parametros;

	protected Funcao(String modificadores, String retorno, String nome, Parametros parametros) {
		super("Funcao");
		this.modificadores = modificadores;
		this.parametros = parametros;
		this.retorno = retorno;
		this.nome = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(modificadores + " " + retorno + " " + nome);
		parametros.gerar(0, pool);
		pool.append(" {").ql();
		super.gerar(tab + 1, pool);
		pool.tab(tab).append("}").ql();
	}
}