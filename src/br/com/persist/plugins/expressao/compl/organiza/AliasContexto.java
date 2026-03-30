package br.com.persist.plugins.expressao.compl.organiza;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;
import br.com.persist.plugins.expressao.compl.Contexto.PontoEVirgula;

public class AliasContexto extends Contexto {
	private TokenExec[] execs = { new ChaveN(), new Chave(), new PontoEVirgula() };
	protected Token pacote;
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
				pacote = token;
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
}