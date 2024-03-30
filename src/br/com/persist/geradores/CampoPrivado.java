package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class CampoPrivado extends Objeto {
	private final Variavel variavel;

	public CampoPrivado(Variavel variavel) {
		super("CampoPrivado");
		this.variavel = variavel;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("private ");
		variavel.gerar(0, pool);
		pool.append(";").ql();
	}
}