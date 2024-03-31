package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class ClassePublica extends Container {
	private final String string;

	protected ClassePublica(String string) {
		super("ClassePublica");
		this.string = string;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append("public class " + string + " {").ql();
		super.gerar(tab, pool);
		pool.append("}");
	}
}