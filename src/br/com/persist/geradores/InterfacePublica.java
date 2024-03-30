package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class InterfacePublica extends Container {
	private final String string;

	public InterfacePublica(String string) {
		super("Interface");
		this.string = string;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append("public interface " + string + " {").ql();
		super.gerar(1, pool);
		pool.append("}");
	}
}