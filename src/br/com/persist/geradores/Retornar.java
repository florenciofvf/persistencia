package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Retornar extends ObjetoString {
	public Retornar(String string) {
		super("Retornar", string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("return " + string).append(";").ql();
	}
}