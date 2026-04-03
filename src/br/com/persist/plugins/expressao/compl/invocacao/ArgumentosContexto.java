package br.com.persist.plugins.expressao.compl.invocacao;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;
import br.com.persist.plugins.expressao.compl.instrucoes.ExpressaoContexto;

public class ArgumentosContexto extends Contexto {
	private TokenExec selecionado = new FinalizaOuArgumento();

	@Context("argumentos_da_funcao")
	@Doc({ "()", "(expressao)", "(argumentos_da_funcao, (expressao))" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		selecionado.processar(compilador, token);
	}

	class FinalizaOuArgumento implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isFechaParentese()) {
				compilador.selecionarParentDe(ArgumentosContexto.this);
			} else if (token.isAbreParentese()) {
				ExpressaoContexto expressao = new ExpressaoContexto();
				compilador.selecionar(expressao);
				add(expressao);
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
				compilador.selecionarParentDe(ArgumentosContexto.this);
			} else if (token.isVirgula()) {
				selecionado = new Argumento();
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class Argumento implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isAbreParentese()) {
				ExpressaoContexto expressao = new ExpressaoContexto();
				compilador.selecionar(expressao);
				add(expressao);
				selecionado = new FinalizaOuVirgula();
			} else {
				compilador.invalidar(token);
			}
		}
	}
}