package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class JSFuncaoAtributo extends ContainerJS {
	private final Parametros parametros;
	private final String nome;

	protected JSFuncaoAtributo(String nome, Parametros parametros) {
		super("JSFuncaoAtributo");
		this.nome = nome;
		this.parametros = parametros;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(nome + " = function");
		parametros.gerar(0, pool);
		pool.append(" {").ql();
		super.gerar(tab, pool);
		pool.tab(tab).append("};").ql();
	}
}