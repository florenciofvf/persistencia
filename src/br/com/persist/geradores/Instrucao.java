package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Instrucao extends ObjetoString {
	public Instrucao(String string) {
		super("Instrucao", string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(string).append(";").ql();
	}
}