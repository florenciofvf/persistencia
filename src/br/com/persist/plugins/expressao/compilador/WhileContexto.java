package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoException;

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