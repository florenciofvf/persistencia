package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoException;

public class ParametrosContexto extends Contexto {
	private TokenExec selecionado = new FinalizaOuParametro();

	@Context("parametros_da_funcao")
	@Doc({ "()", "chave", "parametros_da_funcao, chave" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		selecionado.processar(compilador, token);
	}

	class FinalizaOuParametro implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isFechaParentese()) {
				compilador.setSelecionado(parent);
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
				compilador.setSelecionado(parent);
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
}