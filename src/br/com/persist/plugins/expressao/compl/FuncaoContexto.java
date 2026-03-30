package br.com.persist.plugins.expressao.compl;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;

public class FuncaoContexto extends Contexto {
	private TokenExec[] execs = { new Chave(), new IniParametros(), new TipoRetornoOuIniInstrucoes(),
			new PontoEVirgula() };
	protected boolean retornoVoid;

	@Context("funcao")
	@Doc({ "funcao chave parametros instrucoes;", "funcao parametros void instrucoes;" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		checarIndiceEstado(compilador, execs, token);
		execs[indiceEstado].processar(compilador, token);
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

	class TipoRetornoOuIniInstrucoes implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto(InstrucoesContexto.FUNCAO);
				compilador.setSelecionado(instrucoes);
				add(instrucoes);
				indiceEstado++;
			} else if (ExpressaoConstantes.VOID.equals(token.getString())) {
				execs = new TokenExec[] { new Chave(), new IniParametros(), new TipoRetornoOuIniInstrucoes(),
						new AbreChave(InstrucoesContexto.FUNCAO), new PontoEVirgula() };
				retornoVoid = true;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}