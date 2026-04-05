package br.com.persist.plugins.expressao.compl.funcao;

import java.io.PrintWriter;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.TokenManager;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;

public class ParametrosContexto extends Contexto {
	private TokenExec selecionado = new FinalizaOuParametro();

	@Context("parametros_da_funcao")
	@Doc({ "()", "(chave)", "(parametros_da_funcao, chave)" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		selecionado.processar(tokenManager, token);
	}

	class FinalizaOuParametro implements TokenExec {
		@Override
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isFechaParentese()) {
				tokenManager.selecionarParentDe(ParametrosContexto.this);
			} else if (token.isChave()) {
				adicionar(new ParametroContexto(token));
				selecionado = new FinalizaOuVirgula();
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	class FinalizaOuVirgula implements TokenExec {
		@Override
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isFechaParentese()) {
				tokenManager.selecionarParentDe(ParametrosContexto.this);
			} else if (token.isVirgula()) {
				selecionado = new Parametro();
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	class Parametro implements TokenExec {
		@Override
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isChave()) {
				adicionar(new ParametroContexto(token));
				selecionado = new FinalizaOuVirgula();
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		for (Contexto item : componentes) {
			item.salvar(pw);
		}
	}
}