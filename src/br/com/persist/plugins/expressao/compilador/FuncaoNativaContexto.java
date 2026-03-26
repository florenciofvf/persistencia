package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoConstantes;

public class FuncaoNativaContexto extends Contexto {
	private TokenExec[] execs = { new ChaveN(), new IniParametros(), new TipoRetornoOuPontoEVirgula(),
			new PontoEVirgula() };
	protected boolean retornoVoid;
	protected Token biblioteca;

	@Context("funcao_nativa")
	@Doc({ "funcao_nativa biblioteca parametros;", "funcao_nativa parametros void;" })
	@Override
	public void processar(Compilador compilador, Token token) {
		execs[indiceEstado].processar(compilador, token);
	}

	class ChaveN implements TokenExec {
		public void processar(Compilador compilador, Token token) {
			if (token.isChaveN()) {
				biblioteca = token;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class IniParametros implements TokenExec {
		public void processar(Compilador compilador, Token token) {
			if (token.isAbreParentese()) {
				ParametrosContexto parametros = new ParametrosContexto();
				compilador.setSelecionado(parametros);
				add(parametros);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class TipoRetornoOuPontoEVirgula implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) {
			if (token.isPontoEVirgula()) {
				execs = new TokenExec[] { new ChaveN(), new IniParametros(), new TipoRetornoOuPontoEVirgula() };
				compilador.setSelecionado(parent);
				indiceEstado++;
			} else if (ExpressaoConstantes.VOID.equals(token.getString())) {
				retornoVoid = true;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}