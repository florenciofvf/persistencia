package br.com.persist.plugins.expressao.compl.funcao;

import java.io.PrintWriter;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
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
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		selecionado.processar(compilador, token);
	}

	class FinalizaOuParametro implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isFechaParentese()) {
				compilador.selecionarParentDe(ParametrosContexto.this);
			} else if (token.isChave()) {
				add(new ParametroContexto(token));
				selecionado = new FinalizaOuVirgula();
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class FinalizaOuVirgula implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isFechaParentese()) {
				compilador.selecionarParentDe(ParametrosContexto.this);
			} else if (token.isVirgula()) {
				selecionado = new Parametro();
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class Parametro implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isChave()) {
				add(new ParametroContexto(token));
				selecionado = new FinalizaOuVirgula();
			} else {
				compilador.invalidar(token);
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