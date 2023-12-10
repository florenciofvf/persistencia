package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Atribuir extends Container {
	private final String prefixo;
	private final String esquerdo;
	private final String direito;

	public Atribuir(String prefixo, String esquerdo, String direito) {
		this.prefixo = prefixo;
		this.esquerdo = esquerdo;
		this.direito = direito;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(prefixo + esquerdo + " = " + direito).append(";").ql();
	}
}