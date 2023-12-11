package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Funcao extends Container {
	private final String visibilidade;
	private final String retorno;
	private final String nome;
	private final Parametros parametros;

	public Funcao(String visibilidade, String retorno, String nome, Parametros parametros) {
		this.visibilidade = visibilidade;
		this.retorno = retorno;
		this.nome = nome;
		this.parametros = parametros;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(visibilidade + " " + retorno + " " + nome);
		parametros.gerar(0, pool);
		pool.append(" {").ql();
		for (Container c : lista) {
			c.gerar(tab + 1, pool);
		}
		pool.tab(tab).append("}").ql();
	}
}