package br.com.persist.plugins.expressao.compl.instrucoes;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.biblio.ConstanteContexto;
import br.com.persist.plugins.expressao.compl.cond.IFContexto;
import br.com.persist.plugins.expressao.compl.funcao.RetornoContexto;
import br.com.persist.plugins.expressao.compl.invocacao.InvocacaoContexto;
import br.com.persist.plugins.expressao.compl.loop.WhileContexto;

public class InstrucoesContexto extends Salto {
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		if (token.isReservado()) {
			if (ExpressaoConstantes.CONST.equals(token.getString())) {
				ConstanteContexto constante = new ConstanteContexto();
				compilador.selecionar(constante);
				adicionar(constante);
			} else if (ExpressaoConstantes.RETURN.equals(token.getString())) {
				RetornoContexto retorno = new RetornoContexto();
				compilador.selecionar(retorno);
				adicionar(retorno);
			} else if (ExpressaoConstantes.IF.equals(token.getString())) {
				IFContexto se = new IFContexto();
				compilador.selecionar(se);
				adicionar(se);
			} else if (ExpressaoConstantes.WHILE.equals(token.getString())) {
				WhileContexto loop = new WhileContexto();
				compilador.selecionar(loop);
				adicionar(loop);
			} else {
				compilador.invalidar(token);
			}
		} else if (token.chave()) {
			InvocacaoContexto invocacao = new InvocacaoContexto(token, false);
			compilador.selecionar(invocacao);
			adicionar(invocacao);
		} else if (token.isFechaChave()) {
			compilador.selecionarParentDe(this);
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	protected void configurarSaltosPos() throws ExpressaoException {
		if (parent instanceof WhileContexto) {
			instrucoesGotoWhile();
		} else if (parent instanceof IFContexto) {
			instrucoesGotoIf();
		}
	}
}