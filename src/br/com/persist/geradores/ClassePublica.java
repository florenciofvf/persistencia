package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class ClassePublica extends Container {
	private final String string;

	public ClassePublica(String string) {
		super("ClassePublida");
		this.string = string;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append("public class " + string + " {").ql();
		super.gerar(1, pool);
		pool.append("}");
	}
}