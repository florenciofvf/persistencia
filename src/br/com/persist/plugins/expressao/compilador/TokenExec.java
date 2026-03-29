package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoException;

public interface TokenExec {
	public void processar(Compilador compilador, Token token) throws ExpressaoException;
}