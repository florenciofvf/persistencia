package br.com.persist.plugins.expressao.compl;

import br.com.persist.plugins.expressao.ExpressaoException;

public class ConstanteContexto extends Contexto {
	private TokenExec[] execs = { new Chave(), new Atribuicao(), new AbreParentese(false), new PontoEVirgula() };
	protected Token chave;

	@Context("declaracao_constante")
	@Doc("const chave = expressao;")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		checarIndiceEstado(compilador, execs, token);
		execs[indiceEstado].processar(compilador, token);
	}

	class Atribuicao implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isAtribuicao()) {
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}