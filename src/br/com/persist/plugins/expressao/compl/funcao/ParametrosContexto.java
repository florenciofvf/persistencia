package br.com.persist.plugins.expressao.compl.funcao;

import java.io.PrintWriter;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenManager;

public class ParametrosContexto extends Contexto {
	private static final String ERRO_EXPRESSAO_PARAMETROS_SELECIONADO_VIA = "erro.expressao.parametros.selecionado_via";
	private static final String[] FINALIZADORES = new String[] { ",", ")" };

	@Override
	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
		if (contexto instanceof ParametrosContextoHandler) {
			ParametrosContextoHandler handler = (ParametrosContextoHandler) contexto;
			Token finalizador = handler.getTokenFinalizador();
			if (finalizador == null) {
				throw new ExpressaoException(ERRO_EXPRESSAO_PARAMETROS_SELECIONADO_VIA);
			}
			if (handler.getToken() == null) {
				throw new ExpressaoException(ERRO_EXPRESSAO_PARAMETROS_SELECIONADO_VIA);
			}
			remove(handler);
			adicionar(new ParametroContexto(handler.getToken()));
			if (finalizador.isVirgula()) {
				handler = new ParametrosContextoHandler(FINALIZADORES);
				tokenManager.selecionar(handler);
				adicionar(handler);
			} else if (finalizador.isFechaParentese()) {
				tokenManager.selecionarParentDe(this);
			} else {
				throw new ExpressaoException(ERRO_EXPRESSAO_PARAMETROS_SELECIONADO_VIA);
			}
		} else {
			throw new ExpressaoException(ERRO_EXPRESSAO_PARAMETROS_SELECIONADO_VIA);
		}
	}

	@Override
	protected void processarPre(TokenManager tokenManager, Token token) throws ExpressaoException {
		if (token.isFechaParentese()) {
			if (isEmpty()) {
				token.setConsumido(true);
				tokenManager.selecionarParentDe(this);
			} else {
				tokenManager.invalidar(token);
			}
		} else {
			ParametrosContextoHandler handler = new ParametrosContextoHandler(FINALIZADORES);
			tokenManager.selecionar(handler);
			adicionar(handler);
		}
	}

	@Context("parametros_da_funcao")
	@Doc({ "()", "param", "parametros_da_funcao, param" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		throw new ExpressaoException("erro.processar.parametros.estado");
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		for (Contexto item : componentes) {
			item.salvar(pw);
		}
	}
}