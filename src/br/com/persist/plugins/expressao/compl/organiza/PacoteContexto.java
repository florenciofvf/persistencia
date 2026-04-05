package br.com.persist.plugins.expressao.compl.organiza;

import java.io.PrintWriter;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.TokenManager;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;

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