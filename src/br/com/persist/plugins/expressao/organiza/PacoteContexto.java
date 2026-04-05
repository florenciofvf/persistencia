package br.com.persist.plugins.expressao.organiza;

import java.io.PrintWriter;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenExec;
import br.com.persist.plugins.expressao.compilador.TokenManager;

public class PacoteContexto extends Contexto {
	private TokenExec[] execs = { new ChaveN(), new PontoEVirgula() };
	public static final String PREFIXO_PACKAGE = "package ";
	protected Token pacote;

	@Context("pacote_da_biblioteca")
	@Doc("package chaveN;")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		checarIndiceEstado(tokenManager, execs, token);
		execs[indiceEstado].processar(tokenManager, token);
	}

	class ChaveN implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isChaveN()) {
				pacote = token;
				indiceEstado++;
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	public String getNomeAbsoluto() throws ExpressaoException {
		if (pacote == null) {
			throw new ExpressaoException("Package n\u00E3o definido", false);
		}
		return pacote.getString();
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		pw.println(PREFIXO_PACKAGE + getNomeAbsoluto());
	}
}