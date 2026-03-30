package br.com.persist.plugins.expressao.compl.cond.biblio;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;
import br.com.persist.plugins.expressao.compl.Contexto.AbreParentese;
import br.com.persist.plugins.expressao.compl.Contexto.Chave;
import br.com.persist.plugins.expressao.compl.Contexto.PontoEVirgula;

public class ConstanteContexto extends Contexto {
	private TokenExec[] execs = { new Chave(), new Atribuicao(), new AbreParentese(false), new PontoEVirgula() };
	protected Token chave;

	@Context("declaracao_constante")
	@Doc("const chave = expressao;")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		checarIndiceEstado(compilador, execs, token);
		execs[indiceEstado].processar(compilador, token);
	}

	class Atribuicao implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isAtribuicao()) {
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}