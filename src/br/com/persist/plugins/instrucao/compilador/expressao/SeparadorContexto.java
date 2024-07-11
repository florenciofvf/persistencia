package br.com.persist.plugins.instrucao.compilador.expressao;

import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class SeparadorContexto extends Container {
	public SeparadorContexto(Token token) {
		this.token = token;
	}
}