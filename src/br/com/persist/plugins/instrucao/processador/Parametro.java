package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.compilador.ParametroContexto;
import br.com.persist.plugins.instrucao.compilador.Token;

public class Parametro extends ParametroContexto {
	Object valor;
	int indice;

	public Parametro(Token token) {
		super(token);
	}

	@Override
	public String toString() {
		return indice + ": " + nome + "=" + valor;
	}
}