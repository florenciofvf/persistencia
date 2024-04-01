package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class ClassePrivada extends ContainerJV {
	private final String string;

	protected ClassePrivada(String string) {
		super("ClassePrivada");
		this.string = string;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("private class " + string + " {").ql();
		super.gerar(tab, pool);
		pool.tab(tab).append("}").ql();
	}
}