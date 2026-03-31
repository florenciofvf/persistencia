package br.com.persist.plugins.expressao.compl.organiza;

import java.io.PrintWriter;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;

public class AliasContexto extends Contexto {
	private TokenExec[] execs = { new ChaveN(), new Chave(), new PontoEVirgula() };
	public static final String PREFIXO_ALIAS = "alias ";
	protected Token biblioteca;
	protected Token alias;

	@Context("alias_para_biblioteca")
	@Doc({ "alias chaveN chave;" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		checarIndiceEstado(compilador, execs, token);
		execs[indiceEstado].processar(compilador, token);
	}

	class ChaveN implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isChaveN()) {
				biblioteca = token;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class Chave implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isChave()) {
				alias = token;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	public String getBiblioteca() throws ExpressaoException {
		if (biblioteca == null) {
			throw new ExpressaoException("Biblioteca n\u00E3o definida", false);
		}
		return biblioteca.getString();
	}

	public String getAlias() throws ExpressaoException {
		if (alias == null) {
			throw new ExpressaoException("Alias n\u00E3o definido", false);
		}
		return alias.getString();
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		pw.println(PREFIXO_ALIAS + getBiblioteca() + ExpressaoConstantes.ESPACO + getAlias());
	}
}