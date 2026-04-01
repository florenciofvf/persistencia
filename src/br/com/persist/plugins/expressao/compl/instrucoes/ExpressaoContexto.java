package br.com.persist.plugins.expressao.compl.instrucoes;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.cond.IFContexto;
import br.com.persist.plugins.expressao.compl.invocacao.InvocacaoContexto;
import br.com.persist.plugins.expressao.compl.loop.WhileContexto;
import br.com.persist.plugins.expressao.compl.nativo.ChaveContexto;
import br.com.persist.plugins.expressao.compl.nativo.FlutuanteContexto;
import br.com.persist.plugins.expressao.compl.nativo.InteiroContexto;
import br.com.persist.plugins.expressao.compl.nativo.StringContexto;
import br.com.persist.plugins.expressao.compl.operador.OperadorContexto;

public class ExpressaoContexto extends Salto {
	@Context("expressao")
	@Doc({ "(valor)", "(valor operador valor)", "(valor operador expressao)", "(expressao operador valor)",
			"(expressao operador expressao)" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		if (token.isFechaParentese()) {
			compilador.setSelecionado(parent);
		} else if (token.isAbreParentese()) {
			if (getUltimo() instanceof ChaveContexto) {
				InvocacaoContexto invocacao = new InvocacaoContexto(excluirUltimo().getToken(), true);
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

	@Override
	protected void configurarSaltosPos() throws ExpressaoException {
		if (parent instanceof WhileContexto) {
			expressaoIfEqWhile();
		} else if (parent instanceof IFContexto) {
			expressaoIfEqIf();
		}
	}
}