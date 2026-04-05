package br.com.persist.plugins.expressao.compl.invocacao;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.instrucoes.ExpressaoContexto;

public class ArgumentosContexto extends Contexto {
	private static final String ERRO_EXPRESSAO_ARGUMENTOS_SELECIONADO_VIA = "erro.expressao.argumentos.selecionado_via";
	private static final String[] FINALIZADORES = new String[] { ",", ")" };

	@Override
	protected void selecionadoVia(Compilador compilador, Contexto contexto) throws ExpressaoException {
		if (contexto instanceof ExpressaoContexto) {
			ExpressaoContexto expressao = (ExpressaoContexto) contexto;
			Token token = expressao.getTokenFinalizador();
			if (token == null) {
				throw new ExpressaoException(ERRO_EXPRESSAO_ARGUMENTOS_SELECIONADO_VIA);
			}
			if (token.isVirgula()) {
				expressao = new ExpressaoContexto(FINALIZADORES);
				compilador.selecionar(expressao);
				adicionar(expressao);
			} else if (token.isFechaParentese()) {
				compilador.selecionarParentDe(this);
			} else {
				throw new ExpressaoException(ERRO_EXPRESSAO_ARGUMENTOS_SELECIONADO_VIA);
			}
		} else {
			throw new ExpressaoException(ERRO_EXPRESSAO_ARGUMENTOS_SELECIONADO_VIA);
		}
	}

	@Override
	protected void processarPre(Compilador compilador, Token token) throws ExpressaoException {
		if (token.isFechaParentese()) {
			if (isEmpty()) {
				token.setConsumido(true);
				compilador.selecionarParentDe(this);
			} else {
				compilador.invalidar(token);
			}
		} else {
			ExpressaoContexto expressao = new ExpressaoContexto(FINALIZADORES);
			compilador.selecionar(expressao);
			adicionar(expressao);
		}
	}

	@Context("argumentos_da_funcao")
	@Doc({ "()", "arg", "argumentos_da_funcao, arg" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		throw new ExpressaoException("erro.processar.argumentos.estado");
	}
}