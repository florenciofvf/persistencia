package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoConstantes;

public class IFContexto extends Contexto {
	private TokenExec selecionado = new IniExpressao();

	@Context("se")
	@Doc({ "if expressao instrucoes;", "if expressao instrucoes else instrucoes;",
			"if expressao instrucoes elseif expressao instrucoes;",
			"if expressao instrucoes elseif expressao instrucoes else instrucoes;",
			"if expressao instrucoes elseif expressao instrucoes elseif expressao instrucoes else instrucoes;" })
	@Override
	public void processar(Compilador compilador, Token token) {
		selecionado.processar(compilador, token);
	}

	class IniExpressao implements TokenExec {
		public void processar(Compilador compilador, Token token) {
			if (token.isAbreParentese()) {
				ExpressaoContexto expressao = new ExpressaoContexto();
				compilador.setSelecionado(expressao);
				add(expressao);
				selecionado = new IniInstrucao();
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class IniInstrucao implements TokenExec {
		public void processar(Compilador compilador, Token token) {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto();
				compilador.setSelecionado(instrucoes);
				add(instrucoes);
				selecionado = new FinalizaOuElseOuElseIf();
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class FinalizaOuElseOuElseIf implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) {
			if (token.isPontoEVirgula()) {
				compilador.setSelecionado(parent);
			} else if (ExpressaoConstantes.ELSE.equals(token.getString())) {
				selecionado = new IniInstrucaoElse();
			} else if (ExpressaoConstantes.ELSEIF.equals(token.getString())) {
				selecionado = new IniExpressao();
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class IniInstrucaoElse implements TokenExec {
		public void processar(Compilador compilador, Token token) {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto();
				compilador.setSelecionado(instrucoes);
				add(instrucoes);
				selecionado = new PontoEVirgula();
			} else {
				compilador.invalidar(token);
			}
		}
	}
}