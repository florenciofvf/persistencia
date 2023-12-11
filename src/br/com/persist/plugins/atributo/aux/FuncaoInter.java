package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class FuncaoInter extends Container {
	private final String retorno;
	private final String nome;
	private final Parametros parametros;

	public FuncaoInter(String retorno, String nome, Parametros parametros) {
		this.retorno = retorno;
		this.nome = nome;
		this.parametros = parametros;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(retorno + " " + nome);
		parametros.gerar(0, pool);
		pool.append(";").ql();
	}
}