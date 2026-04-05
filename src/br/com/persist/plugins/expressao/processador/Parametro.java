package br.com.persist.plugins.expressao.processador;

import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.parametros.ParametroContexto;

public class Parametro extends ParametroContexto {
	Object valor;

	/** int indice; */

	public Parametro(Token token) {
		super(token);
	}

	@Override
	public String toString() {
		return indice + ": " + token.getString() + "=" + valor;
	}
}