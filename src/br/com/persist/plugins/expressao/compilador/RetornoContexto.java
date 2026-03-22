package br.com.persist.plugins.expressao.compilador;

public class RetornoContexto extends Contexto {
	@Context("retorno_da_funcao")
	@Doc({ "return;", "return expressao;" })
	@Override
	public void processar(Compilador compilador, Token token) {
		if (token.isPontoEVirgula()) {
			compilador.setSelecionado(parent);
		} else if (token.isAbreParentese()) {
			ExpressaoContexto expressao = new ExpressaoContexto();
			compilador.setSelecionado(expressao);
			add(expressao);
		} else {
			compilador.invalidar(token);
		}
	}
}