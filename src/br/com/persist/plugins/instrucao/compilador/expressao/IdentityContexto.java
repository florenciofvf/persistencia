package br.com.persist.plugins.instrucao.compilador.expressao;

import br.com.persist.plugins.instrucao.compilador.Container;

public class IdentityContexto extends Container {
	private final String id;

	public IdentityContexto(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}