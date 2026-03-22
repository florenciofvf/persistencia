package br.com.persist.plugins.expressao.compilador;

public class AliasContexto extends Contexto {
	private TokenExec[] execs = { new ChaveN(), new Chave(), new PontoEVirgula() };
	protected Token pacote;
	protected Token alias;

	@Context("alias_para_biblioteca")
	@Doc({ "alias chaveN chave;" })
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

	class Chave implements TokenExec {
		public void processar(Compilador compilador, Token token) {
			if (token.isChave()) {
				alias = token;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}