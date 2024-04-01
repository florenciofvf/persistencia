package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Importar extends ObjetoString {
	protected Importar(String string) {
		super("Importar", string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(0).append("import " + string).append(";").ql();
	}
}