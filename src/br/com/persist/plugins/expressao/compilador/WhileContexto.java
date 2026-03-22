package br.com.persist.plugins.expressao.compilador;

public class WhileContexto extends Contexto {
	private TokenExec[] execs = { new AbreParentese(), new AbreChave(), new PontoEVirgula() };

	@Context("loop_while")
	@Doc("while expressao instrucoes;")
	@Override
	public void processar(Compilador compilador, Token token) {
		execs[indiceEstado].processar(compilador, token);
	}
}