package br.com.persist.plugins.expressao.compilador;

public class ExpressaoContexto extends Contexto {
	@Context("expressao")
	@Doc({ "(valor)", "(valor operador valor)", "(valor operador expressao)", "(expressao operador valor)",
			"(expressao operador expressao)" })
	@Override
	public void processar(Compilador compilador, Token token) {
		if (token.isFechaParentese()) {
			compilador.setSelecionado(parent);
		} else if (token.isAbreParentese()) {
			if (getUltimo() instanceof ChaveContexto) {
				InvocacaoContexto invocacao = new InvocacaoContexto(excluirUltimo().token);
				compilador.setSelecionado(invocacao);
				add(invocacao);
			} else {
				ExpressaoContexto expressao = new ExpressaoContexto();
				compilador.setSelecionado(expressao);
				add(expressao);
			}
		} else if (token.isOperador()) {
			add(new OperadorContexto(token));
		} else if (token.isString()) {
			add(new StringContexto(token));
		} else if (token.isInteiro()) {
			add(new InteiroContexto(token));
		} else if (token.isFlutuante()) {
			add(new FlutuanteContexto(token));
		} else if (token.isChave() || token.isChave2()) {
			add(new ChaveContexto(token));
		} else {
			compilador.invalidar(token);
		}
	}
}