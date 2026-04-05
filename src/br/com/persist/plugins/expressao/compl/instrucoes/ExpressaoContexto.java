package br.com.persist.plugins.expressao.compl.instrucoes;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;
import br.com.persist.plugins.expressao.compl.cond.IFContexto;
import br.com.persist.plugins.expressao.compl.invocacao.InvocacaoContexto;
import br.com.persist.plugins.expressao.compl.loop.WhileContexto;
import br.com.persist.plugins.expressao.compl.nativo.ChaveContexto;
import br.com.persist.plugins.expressao.compl.nativo.FlutuanteContexto;
import br.com.persist.plugins.expressao.compl.nativo.InteiroContexto;
import br.com.persist.plugins.expressao.compl.nativo.StringContexto;
import br.com.persist.plugins.expressao.compl.operador.OperadorContexto;

public class ExpressaoContexto extends Salto {
	private TokenExec selecionado = new OperMOuMNativoIniExpressaoChave();
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

	@Context("expressao")
	@Doc({ "(valor)", "(valor operador valor)", "(valor operador expressao)", "(expressao operador valor)",
			"(expressao operador expressao)" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		selecionado.processar(compilador, token);
	}

	@Override
	protected void processarPre(Compilador compilador, Token token) throws ExpressaoException {
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
			montarArvore(compilador);
			compilador.selecionarParentDe(this);
		}
	}

	public Token getTokenFinalizador() {
		return tokenFinalizador;
	}

	class OperMOuMNativoIniExpressaoChave implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isOperadorMOuM()) {
				processarOperadorMOuM(token);
			} else if (token.isNativo()) {
				processarNativo(compilador, token);
			} else if (token.isAbreParentese()) {
				processarIniExpressao(compilador, token);
			} else if (token.chave()) {
				processarChave(token);
			} else {
				compilador.invalidar(token);
			}
		}

		private void processarOperadorMOuM(Token token) {
			tokenMOuM = token;
			selecionado = new NativoIniExpressaoChave();
		}
	}

	class NativoIniExpressaoChave implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isNativo()) {
				processarNativo(compilador, token);
			} else if (token.isAbreParentese()) {
				processarIniExpressao(compilador, token);
			} else if (token.chave()) {
				processarChave(token);
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class QQOperador implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isOperador()) {
				OperadorContexto operador = new OperadorContexto(token);
				adicionar(operador);
				selecionado = new OperMOuMNativoIniExpressaoChave();
			} else {
				compilador.invalidar(token);
			}
		}
	}

	private void processarNativo(Compilador compilador, Token token) throws ExpressaoException {
		Contexto nativo = criarNativo(token);
		if (tokenMOuM != null && nativo instanceof StringContexto) {
			compilador.invalidar(token);
		}
		if (tokenMOuM != null) {
			nativo.negativar(tokenMOuM);
			tokenMOuM = null;
		}
		adicionar(nativo);
		selecionado = new QQOperador();
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

	private void processarIniExpressao(Compilador compilador, Token token) throws ExpressaoException {
		if (getUltimo() instanceof ChaveContexto) {
			Contexto ultimo = excluirUltimo();
			InvocacaoContexto invocacao = new InvocacaoContexto(ultimo.getToken(), true);
			invocacao.setNegativoContexto(ultimo.getNegativoContexto());
			compilador.selecionar(invocacao);
			adicionar(invocacao);
			invocacao.processar(compilador, token);
		} else {
			ExpressaoContexto expressao = new ExpressaoContexto();
			compilador.selecionar(expressao);
			if (tokenMOuM != null) {
				expressao.negativar(tokenMOuM);
				tokenMOuM = null;
			}
			adicionar(expressao);
		}
		selecionado = new OperMOuMNativoIniExpressaoChave();
	}

	private void processarChave(Token token) throws ExpressaoException {
		ChaveContexto chave = new ChaveContexto(token);
		if (tokenMOuM != null) {
			chave.negativar(tokenMOuM);
			tokenMOuM = null;
		}
		adicionar(chave);
		selecionado = new OperMOuMNativoIniExpressaoChave();
	}

	@Override
	protected void configurarSaltosPos() throws ExpressaoException {
		if (parent instanceof WhileContexto) {
			expressaoIfEqWhile();
		} else if (parent instanceof IFContexto) {
			expressaoIfEqIf();
		}
	}

	private void montarArvore(Compilador compilador) throws ExpressaoException {
		if (isEmpty()) {
			throw new ExpressaoException("erro.expressao.vazio");
		}
		if (getPrimeiro() instanceof OperadorContexto) {
			compilador.invalidar(getPrimeiro().getToken());
		} else if (getUltimo() instanceof OperadorContexto) {
			compilador.invalidar(getUltimo().getToken());
		}
		validarSequencia(compilador);
		montarArvore();
	}

	private void validarSequencia(Compilador compilador) throws ExpressaoException {
		for (int i = 0; i < getSize(); i++) {
			Contexto c = get(i);
			if (i % 2 == 0) {
				if (c instanceof OperadorContexto) {
					compilador.invalidar(c.getToken());
				}
			} else {
				if (!(c instanceof OperadorContexto)) {
					compilador.invalidar(c.getToken());
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