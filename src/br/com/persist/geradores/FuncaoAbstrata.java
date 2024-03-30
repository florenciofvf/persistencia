package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class FuncaoAbstrata extends Objeto {
	private final String retorno;
	private final String nome;
	private final Parametros parametros;

	public FuncaoAbstrata(String retorno, String nome, Parametros parametros) {
		super("FuncaoAbstrata");
		this.parametros = parametros;
		this.retorno = retorno;
		this.nome = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(retorno + " " + nome);
		parametros.gerar(0, pool);
		pool.append(";").ql();
	}
}