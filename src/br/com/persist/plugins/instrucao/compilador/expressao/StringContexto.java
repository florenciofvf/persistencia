package br.com.persist.plugins.instrucao.compilador.expressao;

import br.com.persist.plugins.instrucao.compilador.Container;

public class StringContexto extends Container {
	private final String string;

	public StringContexto(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}
}