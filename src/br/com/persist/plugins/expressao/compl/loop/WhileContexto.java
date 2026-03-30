package br.com.persist.plugins.expressao.compl.loop;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;
import br.com.persist.plugins.expressao.compl.instrucoes.InstrucoesContexto;

public class WhileContexto extends Contexto {
	private TokenExec[] execs = { new AbreParentese(true), new AbreChave(InstrucoesContexto.LOOP),
			new PontoEVirgula() };

	@Context("loop_while")
	@Doc("while expressao instrucoes;")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		checarIndiceEstado(compilador, execs, token);
		execs[indiceEstado].processar(compilador, token);
	}
}