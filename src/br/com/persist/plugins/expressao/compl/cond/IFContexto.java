package br.com.persist.plugins.expressao.compl.cond;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;
import br.com.persist.plugins.expressao.compl.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.compl.instrucoes.InstrucoesContexto;

public class IFContexto extends Contexto {
	private TokenExec selecionado = new IniExpressao();

	@Context("se")
	@Doc({ "if expressao instrucoes;", "if expressao instrucoes else instrucoes;",
			"if expressao instrucoes elseif expressao instrucoes;",
			"if expressao instrucoes elseif expressao instrucoes else instrucoes;",
			"if expressao instrucoes elseif expressao instrucoes elseif expressao instrucoes else instrucoes;" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		selecionado.processar(compilador, token);
	}

	class IniExpressao implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
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
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
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
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
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
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
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

	@Override
	public Contexto getApos(Contexto contexto) throws ExpressaoException {
		if (!(contexto instanceof ExpressaoContexto)) {
			throw new ExpressaoException("erro.if.expressao.invalida", contexto.toString());
		}
		Contexto instrucoes = super.getApos(contexto);
		if (instrucoes == null) {
			throw new ExpressaoException("erro.if.expressao_sem_instrucao", toString());
		}
		if (!(instrucoes instanceof InstrucoesContexto)) {
			throw new ExpressaoException("erro.estrutura.expressao.invalida", toString());
		}
		return super.getApos(instrucoes);
	}
}