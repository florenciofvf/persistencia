package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Atribuir extends Objeto {
	private final String esquerdo;
	private final String direito;

	protected Atribuir(String esquerdo, String direito) {
		super("Atribuir");
		this.esquerdo = esquerdo;
		this.direito = direito;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(esquerdo + " = " + direito).append(";").ql();
	}
}