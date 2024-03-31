package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Catch extends Container {
	private final String excecao;

	public Catch(String excecao) {
		super("Catch");
		this.excecao = excecao;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("} catch (" + excecao + ") {").ql();
		super.gerar(tab, pool);
		pool.tab(tab).append("}").ql();
	}
}