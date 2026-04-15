package br.com.persist.plugins.expressao.lista;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.Token.Tipo;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.invocacao.InvocacaoContexto;

public class ListaContexto extends Contexto {
	private static final String ERRO_EXPRESSAO_LISTA_SELECIONADO_VIA = "erro.expressao.lista.selecionado_via";
	private static final String[] FINALIZADORES = new String[] { ",", "]" };

	public ListaContexto() {
		adicionar2(new InvocacaoContexto(new Token("list.create", Tipo.VIRTUAL, -1), true));
	}

	@Override
	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
		if (contexto instanceof ExpressaoContexto) {
			ExpressaoContexto expressao = (ExpressaoContexto) contexto;
			Token token = expressao.getTokenFinalizador();
			if (token == null) {
				throw new ExpressaoException(ERRO_EXPRESSAO_LISTA_SELECIONADO_VIA);
			}
			if (token.isVirgula()) {
				expressao = new ExpressaoContexto(FINALIZADORES);
				tokenManager.selecionar(expressao);
				adicionar(expressao);
				adicionarAddItemListaContexto();
			} else if (token.isFechaColchete()) {
				tokenManager.selecionarParentDe(this);
			} else {
				throw new ExpressaoException(ERRO_EXPRESSAO_LISTA_SELECIONADO_VIA);
			}
		} else {
			throw new ExpressaoException(ERRO_EXPRESSAO_LISTA_SELECIONADO_VIA);
		}
	}

	@Override
	protected void processarPre(TokenManager tokenManager, Token token) throws ExpressaoException {
		if (token.isFechaColchete()) {
			if (getSize() == 1) {
				token.setConsumido(true);
				tokenManager.selecionarParentDe(this);
			} else {
				tokenManager.invalidar(token);
			}
		} else {
			ExpressaoContexto expressao = new ExpressaoContexto(FINALIZADORES);
			tokenManager.selecionar(expressao);
			adicionar(expressao);
			adicionarAddItemListaContexto();
		}
	}

	private void adicionarAddItemListaContexto() throws ExpressaoException {
		adicionar(new AddItemListaContexto());
	}

	@Context("lista")
	@Doc({ "[]", "[1]", "[...]" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		throw new ExpressaoException("erro.processar.lista.estado");
	}
}