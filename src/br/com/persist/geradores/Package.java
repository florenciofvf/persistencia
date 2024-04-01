package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Package extends ObjetoString {
	protected Package(String string) {
		super("Package", string);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(0).append("package " + string).append(";").ql();
	}
}