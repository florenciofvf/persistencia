package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class RetornoClasseAnonima extends ContainerJV {
	private final String nome;

	protected RetornoClasseAnonima(String nome) {
		super("RetornoClasseAnonima");
		this.nome = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("return new " + nome + "()").append(" {").ql();
		super.gerar(tab, pool);
		pool.tab(tab).append("};").ql();
	}
}