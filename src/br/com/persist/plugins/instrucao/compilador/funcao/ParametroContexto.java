package br.com.persist.plugins.instrucao.compilador.funcao;

import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class ParametroContexto extends Container {
	private final String nome;

	public ParametroContexto(Token token) {
		this.nome = token.getString();
		this.token = token;
	}

	public String getNome() {
		return nome;
	}
}