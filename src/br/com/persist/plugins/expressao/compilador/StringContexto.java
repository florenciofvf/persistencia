package br.com.persist.plugins.expressao.compilador;

import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;

public class StringContexto extends Contexto {
	public StringContexto(Token token) {
		this.token = token;
	}

	@Context("string")
	@Doc("'xyz'")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		compilador.invalidar(token);
	}

	@Override
	public void empilharLocal(List<Contexto> lista) {
		lista.add(this);
	}
}