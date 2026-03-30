package br.com.persist.plugins.expressao.compl;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;

public class FuncaoNativaContexto extends Contexto {
	private TokenExec[] execs = { new Chave(), new ChaveN(), new IniParametros(), new TipoRetornoOuPontoEVirgula(),
			new PontoEVirgula() };
	protected boolean retornoVoid;
	protected Token biblioteca;

	@Context("funcao_nativa")
	@Doc({ "funcao_nativa chave biblioteca parametros;", "funcao_nativa parametros void;" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		checarIndiceEstado(compilador, execs, token);
		execs[indiceEstado].processar(compilador, token);
	}

	class ChaveN implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isChaveN()) {
				biblioteca = token;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class IniParametros implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
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
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isPontoEVirgula()) {
				execs = new TokenExec[] { new Chave(), new ChaveN(), new IniParametros(),
						new TipoRetornoOuPontoEVirgula() };
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