package br.com.persist.plugins.expressao.compl.cond;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.TokenManager;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;
import br.com.persist.plugins.expressao.compl.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.compl.instrucoes.InstrucoesContexto;

public class IFContexto extends Contexto {
	private TokenExec selecionado = new IniExpressaoIF();

	@Override
	protected void processarPre(TokenManager tokenManager, Token token) throws ExpressaoException {
		if (selecionado == null) {
			tokenManager.selecionarParentDe(this);
		}
		if (selecionado instanceof ElseOuElseIf && !valido(token)) {
			tokenManager.selecionarParentDe(this);
		}
	}

	private boolean valido(Token token) {
		return ExpressaoConstantes.ELSE.equals(token.getString())
				|| ExpressaoConstantes.ELSEIF.equals(token.getString());
	}

	@Context("se")
	@Doc({ "if expressao instrucoes", "if expressao instrucoes else instrucoes",
			"if expressao instrucoes elseif expressao instrucoes",
			"if expressao instrucoes elseif expressao instrucoes else instrucoes",
			"if expressao instrucoes elseif expressao instrucoes elseif expressao instrucoes else instrucoes" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		selecionado.processar(tokenManager, token);
	}

	class IniExpressaoIF implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isAbreParentese()) {
				ExpressaoContexto expressao = new ExpressaoContexto();
				tokenManager.selecionar(expressao);
				adicionar(expressao);
				selecionado = new IniInstrucoesIF();
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	class IniInstrucoesIF implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto();
				tokenManager.selecionar(instrucoes);
				adicionar(instrucoes);
				selecionado = new ElseOuElseIf();
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	class ElseOuElseIf implements TokenExec {
		@Override
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (ExpressaoConstantes.ELSE.equals(token.getString())) {
				selecionado = new IniInstrucaoElse();
			} else if (ExpressaoConstantes.ELSEIF.equals(token.getString())) {
				selecionado = new IniExpressaoIF();
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	class IniInstrucaoElse implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto();
				tokenManager.selecionar(instrucoes);
				adicionar(instrucoes);
				selecionado = null;
			} else {
				tokenManager.invalidar(token);
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