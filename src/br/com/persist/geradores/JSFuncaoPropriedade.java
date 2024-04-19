package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class JSFuncaoPropriedade extends ContainerJS {
	private final Parametros parametros;
	private final String nome;
	private boolean separar;

	protected JSFuncaoPropriedade(boolean separar, String nome, Parametros parametros) {
		super("JSFuncaoPropriedade");
		this.nome = nome;
		this.separar = separar;
		this.parametros = parametros;
	}

	public void setSeparar(boolean separar) {
		this.separar = separar;
	}

	public boolean isSeparar() {
		return separar;
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