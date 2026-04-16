package br.com.persist.plugins.expressao.loop;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenExec;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.instrucoes.InstrucoesContexto;

public class WhileContexto extends Contexto {
	private TokenExec[] execs = { new IniExpressao(), new IniInstrucoes(false) };

	@Override
	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
		if (contexto instanceof InstrucoesContexto) {
			tokenManager.selecionarParentDe(this);
		}
	}

	@Context("loop_while")
	@Doc("while expressao instrucoes")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		checarIndiceEstado(tokenManager, execs, token);
		execs[indiceEstado].processar(tokenManager, token);
	}
}