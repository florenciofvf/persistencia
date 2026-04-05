package br.com.persist.plugins.expressao.compl;

import br.com.persist.plugins.expressao.ExpressaoException;

public interface TokenExec {
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException;
}