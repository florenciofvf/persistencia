package br.com.persist.plugins.instrucao.compilador.expressao;

import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class IdentityContexto extends Container {
	private final String id;

	public IdentityContexto(Token token) {
		this.id = token.getString();
		this.token = token;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return id;
	}
}