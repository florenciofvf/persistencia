package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class CampoConstante extends Objeto {
	private final Variavel variavel;
	private final String valor;

	protected CampoConstante(Variavel variavel, String valor) {
		super("CampoConstante");
		this.variavel = variavel;
		this.valor = valor;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("public static final ");
		variavel.gerar(0, pool);
		pool.append(" = ").append(valor);
		pool.append(";").ql();
	}
}