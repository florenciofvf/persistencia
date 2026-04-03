package br.com.persist.plugins.expressao.compl.instrucoes;

import java.util.Iterator;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
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
			montarArvore(compilador);
			compilador.selecionarParentDe(this);
		} else if (token.isAbreParentese()) {
			if (getUltimo() instanceof ChaveContexto) {
				InvocacaoContexto invocacao = new InvocacaoContexto(excluirUltimo().getToken(), true);
				compilador.selecionar(invocacao);
				add(invocacao);
			} else {
				ExpressaoContexto expressao = new ExpressaoContexto();
				compilador.selecionar(expressao);
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
		} else if (token.isChave() || token.isChave2() || token.isChaveN()) {
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
		Contexto sel = it.next();
		it.remove();

		if (it.hasNext()) {
			OperadorContexto operador = (OperadorContexto) it.next();
			it.remove();
			operador.add(sel);
			Contexto c = it.next();
			it.remove();
			operador.add(c);
			sel = operador;
		}

		while (it.hasNext()) {
			OperadorContexto operador = (OperadorContexto) it.next();
			it.remove();

			OperadorContexto selecionado = (OperadorContexto) sel;
			if (operador.possuoPrioridadeSobre(selecionado)) {
				Contexto ultimo = selecionado.excluirUltimo();
				operador.add(ultimo);
				selecionado.add(operador);
				Contexto c = it.next();
				it.remove();
				operador.add(c);
			} else {
				operador.add(selecionado);
				Contexto c = it.next();
				it.remove();
				operador.add(c);
				sel = operador;
			}
		}

		if (getSize() != 0 || sel == null) {
			throw new ExpressaoException("erro.expressao_invalida");
		}

		add(sel);
	}
}