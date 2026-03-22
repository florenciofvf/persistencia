package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoConstantes;

public class InstrucoesContexto extends Contexto {
	@Override
	public void processar(Compilador compilador, Token token) {
		if (token.isReservado()) {
			if (ExpressaoConstantes.CONST.equals(token.getString())) {
				ConstanteContexto constante = new ConstanteContexto();
				compilador.setSelecionado(constante);
				add(constante);
			} else if (ExpressaoConstantes.RETURN.equals(token.getString())) {
				RetornoContexto retorno = new RetornoContexto();
				compilador.setSelecionado(retorno);
				add(retorno);
			} else if (ExpressaoConstantes.IF.equals(token.getString())) {
				IFContexto se = new IFContexto();
				compilador.setSelecionado(se);
				add(se);
			} else if (ExpressaoConstantes.WHILE.equals(token.getString())) {
				WhileContexto loop = new WhileContexto();
				compilador.setSelecionado(loop);
				add(loop);
			} else {
				compilador.invalidar(token);
			}
		} else {
			compilador.invalidar(token);
		}
	}
}