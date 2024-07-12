package br.com.persist.plugins.instrucao.compilador.expressao;

import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class StringContexto extends Container {
	private final String string;

	public StringContexto(Token token) {
		this.string = token.getString();
		this.token = token;
	}

	public String getString() {
		return string;
	}

	@Override
	public String toString() {
		return string;
	}
}