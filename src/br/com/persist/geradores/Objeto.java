package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public abstract class Objeto {
	protected final String id;
	protected Container parent;

	protected Objeto(String id) {
		this.id = id;
	}

	public void gerar(int tab, StringPool pool) {
	}

	public Container getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return id;
	}
}