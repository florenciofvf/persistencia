package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class ConstrutorPublico extends Container {
	private final String nome;
	private final Parametros parametros;

	protected ConstrutorPublico(String nome, Parametros parametros) {
		super("ConstrutorPublico");
		this.parametros = parametros;
		this.nome = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("public " + nome);
		parametros.gerar(0, pool);
		pool.append(" {").ql();
		super.gerar(tab + 1, pool);
		pool.tab(tab).append("}").ql();
	}
}