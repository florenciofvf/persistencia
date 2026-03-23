package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoConstantes;

public class FuncaoContexto extends Contexto {
	private TokenExec[] execs = { new IniParametros(), new TipoRetornoOuIniInstrucoes(), new PontoEVirgula() };
	protected boolean retornoVoid;

	@Context("funcao")
	@Doc({ "funcao parametros instrucoes;", "funcao parametros void instrucoes;" })
	@Override
	public void processar(Compilador compilador, Token token) {
		execs[indiceEstado].processar(compilador, token);
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

	class TipoRetornoOuIniInstrucoes implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto();
				compilador.setSelecionado(instrucoes);
				add(instrucoes);
				indiceEstado++;
			} else if (ExpressaoConstantes.VOID.equals(token.getString())) {
				execs = new TokenExec[] { new IniParametros(), new TipoRetornoOuIniInstrucoes(), new AbreChave(),
						new PontoEVirgula() };
				retornoVoid = true;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}