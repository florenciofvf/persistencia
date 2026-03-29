package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoException;

public class InteiroContexto extends Contexto {
	public InteiroContexto(Token token) {
		this.token = token;
	}

	@Context("inteiro")
	@Doc("123")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		compilador.invalidar(token);
	}
}