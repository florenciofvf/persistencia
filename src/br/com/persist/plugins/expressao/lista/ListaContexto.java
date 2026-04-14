package br.com.persist.plugins.expressao.lista;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.Token.Tipo;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.invocacao.InvocacaoContexto;
import br.com.persist.plugins.expressao.operador.OperadorContexto;

public class ListaContexto extends Contexto {
	private static final String ERRO_EXPRESSAO_LISTA_SELECIONADO_VIA = "erro.expressao.lista.selecionado_via";
	private static final String[] FINALIZADORES = new String[] { ",", "]" };

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
			} else if (token.isFechaColchete()) {
				montarArvore(tokenManager);
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
			if (isEmpty()) {
				InvocacaoContexto listaVazia = new InvocacaoContexto(new Token("list.create", Tipo.VIRTUAL, -1), true);
				adicionar(listaVazia);
				token.setConsumido(true);
				tokenManager.selecionarParentDe(this);
			} else {
				tokenManager.invalidar(token);
			}
		} else {
			ExpressaoContexto expressao = new ExpressaoContexto(FINALIZADORES);
			tokenManager.selecionar(expressao);
			adicionar(expressao);
		}
	}

	@Context("lista")
	@Doc({ "[]", "[1]", "[...]" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		throw new ExpressaoException("erro.processar.argumentos.estado");
	}

	private void montarArvore(TokenManager tokenManager) throws ExpressaoException {
		if (isEmpty()) {
			throw new ExpressaoException("erro.lista.vazia");
		}
		List<Contexto> contextos = new ArrayList<>();
		while (!isEmpty()) {
			contextos.add(excluirPrimeiro());
		}
		if (contextos.size() == 1) {
			OperadorContexto listaCom1 = new OperadorContexto(
					new Token(OperadorContexto.CREATE_LISTA1, Tipo.VIRTUAL, -1));
			listaCom1.adicionar(contextos.get(0));
			adicionar(listaCom1);
			return;
		}
		OperadorContexto raiz = null;
		for (Contexto item : contextos) {
			raiz = processar(tokenManager, item, raiz);
		}
		adicionar(raiz);
	}

	private OperadorContexto processar(TokenManager tokenManager, Contexto item, OperadorContexto operador)
			throws ExpressaoException {
		if (operador == null) {
			operador = new OperadorContexto(new Token(":", Tipo.VIRTUAL, -1));
			operador.adicionar(item);
		} else if (operador.getSize() == 1) {
			operador.adicionar(item);
		} else if (operador.getSize() == 2) {
			OperadorContexto novo = new OperadorContexto(new Token(":", Tipo.VIRTUAL, -1));
			novo.adicionar(operador);
			novo.adicionar(item);
			operador = novo;
		} else {
			tokenManager.invalidar();
		}
		return operador;
	}
}