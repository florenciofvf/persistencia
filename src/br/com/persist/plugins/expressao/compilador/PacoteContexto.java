package br.com.persist.plugins.expressao.compilador;

public class PacoteContexto extends Contexto {
	private TokenExec[] execs = { new ChaveN(), new PontoEVirgula() };
	protected Token pacote;

	@Context("pacote_da_biblioteca")
	@Doc({ "package chaveN;" })
	@Override
	public void processar(Compilador compilador, Token token) {
		execs[indiceEstado].processar(compilador, token);
	}

	class ChaveN implements TokenExec {
		public void processar(Compilador compilador, Token token) {
			if (token.isChaveN()) {
				pacote = token;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}