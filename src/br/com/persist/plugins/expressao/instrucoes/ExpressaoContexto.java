package br.com.persist.plugins.expressao.instrucoes;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenExec;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.condicional.IFContexto;
import br.com.persist.plugins.expressao.invocacao.InvocacaoContexto;
import br.com.persist.plugins.expressao.loop.WhileContexto;
import br.com.persist.plugins.expressao.nativo.ChaveContexto;
import br.com.persist.plugins.expressao.nativo.FlutuanteContexto;
import br.com.persist.plugins.expressao.nativo.InteiroContexto;
import br.com.persist.plugins.expressao.nativo.StringContexto;
import br.com.persist.plugins.expressao.operador.OperadorContexto;

public class ExpressaoContexto extends Salto {
	private TokenExec selecionado = new OperadorMouMNativoIniExpressaoChave();
	private final String[] finalizadores;
	private Token tokenFinalizador;
	private Token tokenMOuM;

	public ExpressaoContexto(String[] finalizadores) {
		this.finalizadores = Objects.requireNonNull(finalizadores);
	}

	public ExpressaoContexto(String finalizador) {
		this(new String[] { finalizador });
	}

	public ExpressaoContexto() {
		this(new String[] { ")" });
	}

	@Override
	protected void processarPre(TokenManager tokenManager, Token token) throws ExpressaoException {
		String string = token.getString();
		boolean finalizar = false;
		for (String item : finalizadores) {
			if (item.equals(string)) {
				finalizar = true;
			}
		}
		if (finalizar) {
			token.setConsumido(true);
			tokenFinalizador = token;
			montarArvore(tokenManager);
			tokenManager.selecionarParentDe(this);
		}
	}

	public Token getTokenFinalizador() {
		return tokenFinalizador;
	}

	@Context("expressao")
	@Doc({ "(valor)", "(valor operador valor)", "(valor operador expressao)", "(expressao operador valor)",
			"(expressao operador expressao)" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		selecionado.processar(tokenManager, token);
	}

	class OperadorMouMNativoIniExpressaoChave implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isOperadorMOuM()) {
				processarOperadorMOuM(token);
			} else if (token.isNativo()) {
				processarNativo(tokenManager, token);
			} else if (token.isAbreParentese()) {
				processarIniExpressao(tokenManager, token);
			} else if (token.chave()) {
				processarChave(token);
			} else {
				tokenManager.invalidar(token);
			}
		}

		private void processarOperadorMOuM(Token token) {
			tokenMOuM = token;
			selecionado = new NativoIniExpressaoChave();
		}
	}

	class NativoIniExpressaoChave implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isNativo()) {
				processarNativo(tokenManager, token);
			} else if (token.isAbreParentese()) {
				processarIniExpressao(tokenManager, token);
			} else if (token.chave()) {
				processarChave(token);
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	class QQOperadorOuIniInvocacao implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isOperador()) {
				new QQOperador().processar(tokenManager, token);
			} else if (token.isAbreParentese()) {
				processarIniInvocacao(tokenManager, token);
			} else {
				tokenManager.invalidar(token);
			}
		}

		private void processarIniInvocacao(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (getUltimo() instanceof ChaveContexto) {
				Contexto ultimo = excluirUltimo();
				InvocacaoContexto invocacao = new InvocacaoContexto(ultimo.getToken(), true);
				invocacao.setNegativoContexto(ultimo.getNegativoContexto());
				tokenManager.selecionar(invocacao);
				adicionar(invocacao);
				tokenManager.processar(token);
			} else {
				tokenManager.invalidar(token);
			}
			selecionado = new QQOperador();
		}
	}

	class QQOperador implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isOperador()) {
				OperadorContexto operador = new OperadorContexto(token);
				adicionar(operador);
				selecionado = new OperadorMouMNativoIniExpressaoChave();
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	private void processarNativo(TokenManager tokenManager, Token token) throws ExpressaoException {
		Contexto nativo = criarNativo(token);
		if (tokenMOuM != null && nativo instanceof StringContexto) {
			tokenManager.invalidar(token);
		}
		if (tokenMOuM != null) {
			nativo.negativar(tokenMOuM);
			tokenMOuM = null;
		}
		adicionar(nativo);
		selecionado = new QQOperador();
	}

	private void processarIniExpressao(TokenManager tokenManager, Token token) throws ExpressaoException {
		if (getUltimo() instanceof ChaveContexto) {
			tokenManager.invalidar(token);
		} else {
			ExpressaoContexto expressao = new ExpressaoContexto();
			tokenManager.selecionar(expressao);
			if (tokenMOuM != null) {
				expressao.negativar(tokenMOuM);
				tokenMOuM = null;
			}
			adicionar(expressao);
		}
		selecionado = new QQOperador();
	}

	private void processarChave(Token token) throws ExpressaoException {
		ChaveContexto chave = new ChaveContexto(token);
		if (tokenMOuM != null) {
			chave.negativar(tokenMOuM);
			tokenMOuM = null;
		}
		adicionar(chave);
		selecionado = new QQOperadorOuIniInvocacao();
	}

	private Contexto criarNativo(Token token) {
		if (token.isString()) {
			return new StringContexto(token);
		} else if (token.isInteiro()) {
			return new InteiroContexto(token);
		} else {
			return new FlutuanteContexto(token);
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

	private void montarArvore(TokenManager tokenManager) throws ExpressaoException {
		if (isEmpty()) {
			throw new ExpressaoException("erro.expressao.vazio");
		}
		if (getPrimeiro() instanceof OperadorContexto) {
			tokenManager.invalidar(getPrimeiro().getToken());
		} else if (getUltimo() instanceof OperadorContexto) {
			tokenManager.invalidar(getUltimo().getToken());
		}
		validarSequencia(tokenManager);
		montarArvore();
	}

	private void validarSequencia(TokenManager tokenManager) throws ExpressaoException {
		for (int i = 0; i < getSize(); i++) {
			Contexto c = get(i);
			if (i % 2 == 0) {
				if (c instanceof OperadorContexto) {
					tokenManager.invalidar(c.getToken());
				}
			} else {
				if (!(c instanceof OperadorContexto)) {
					tokenManager.invalidar(c.getToken());
				}
			}
		}
	}

	private void montarArvore() throws ExpressaoException {
		Iterator<Contexto> it = componentes.iterator();
		Contexto contextoSel = it.next();
		it.remove();

		if (it.hasNext()) {
			OperadorContexto operador = (OperadorContexto) it.next();
			it.remove();
			operador.adicionar(contextoSel);
			Contexto c = it.next();
			it.remove();
			operador.adicionar(c);
			contextoSel = operador;
		}

		while (it.hasNext()) {
			OperadorContexto operador = (OperadorContexto) it.next();
			it.remove();

			OperadorContexto operadorSel = (OperadorContexto) contextoSel;
			if (operador.possuoPrioridadeSobre(operadorSel)) {
				Contexto ultimo = operadorSel.excluirUltimo();
				operador.adicionar(ultimo);
				operadorSel.adicionar(operador);
				Contexto c = it.next();
				it.remove();
				operador.adicionar(c);
			} else {
				operador.adicionar(operadorSel);
				Contexto c = it.next();
				it.remove();
				operador.adicionar(c);
				contextoSel = operador;
			}
		}

		if (getSize() != 0 || contextoSel == null) {
			throw new ExpressaoException("erro.expressao_invalida");
		}

		adicionar(contextoSel);
	}

	@Override
	protected void empilharLocalPos(List<Contexto> lista) {
		empilharLocalNegativo(lista);
	}

	@Override
	protected void listarPos(List<Contexto> lista) {
		listarNegativo(lista);
	}
}