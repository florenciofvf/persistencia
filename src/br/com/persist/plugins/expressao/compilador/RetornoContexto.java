package br.com.persist.plugins.expressao.compilador;

public class RetornoContexto extends Contexto {
	private TokenExec[] execs = { new PontoEVirgulaOuAbreParentese(), new PontoEVirgula() };

	@Context("retorno_da_funcao")
	@Doc({ "return;", "return expressao;" })
	@Override
	public void processar(Compilador compilador, Token token) {
		execs[indiceEstado].processar(compilador, token);
	}

	class PontoEVirgulaOuAbreParentese implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) {
			if (token.isPontoEVirgula()) {
				compilador.setSelecionado(parent);
				indiceEstado++;
			} else if (token.isAbreParentese()) {
				ExpressaoContexto expressao = new ExpressaoContexto();
				compilador.setSelecionado(expressao);
				add(expressao);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}