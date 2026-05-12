package br.com.persist.plugins.expressao.instrucoes;

import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.condicional.IFContexto;
import br.com.persist.plugins.expressao.funcao.FuncaoContexto;
import br.com.persist.plugins.expressao.loop.WhileContexto;
import br.com.persist.plugins.expressao.retorno.RetornoContexto;
import br.com.persist.plugins.expressao.salto.GotoContexto;
import br.com.persist.plugins.expressao.salto.IFEqContexto;

public abstract class Salto extends Contexto {
	private static final String ERRO_ESTRUTURA_EXPRESSAO_INVALIDA = "erro.estrutura.expressao.invalida";

	protected Salto(Token token) {
		super(token);
	}

	protected Salto() {
		super();
	}

	private void checarFuncaoSemRetorno(Contexto contexto) throws ExpressaoException {
		if (contexto.getParent() instanceof FuncaoContexto) {
			FuncaoContexto fn = (FuncaoContexto) contexto.getParent();
			throw new ExpressaoException("erro.funcao.sem_retorno", fn.getNome());
		}
	}

	private void checarParentInstrucoes(Contexto contexto) throws ExpressaoException {
		if (!(contexto instanceof InstrucoesContexto)) {
			throw new ExpressaoException("erro.estrutura.sem_parent", toString());
		}
	}

	private void checarPilha(List<Contexto> pilha) throws ExpressaoException {
		if (pilha.isEmpty()) {
			throw new ExpressaoException("erro.pilhaLocal.vazio");
		}
	}

	protected void checarVazioInstrucoes() throws ExpressaoException {
		if (isEmpty()) {
			throw new ExpressaoException("erro.instrucoes.vazio");
		}
	}

	private void checarVazioExpressao() throws ExpressaoException {
		if (isEmpty()) {
			throw new ExpressaoException("erro.expressao.vazio");
		}
	}

	protected void expressaoIfEqWhile() throws ExpressaoException {
		if (!(parent instanceof WhileContexto)) {
			throw new ExpressaoException(ERRO_ESTRUTURA_EXPRESSAO_INVALIDA);
		}
		checarVazioExpressao();
		Contexto seOUloop = parent;
		Contexto instrucoes = seOUloop.getParent();
		checarParentInstrucoes(instrucoes);
		Contexto contextoApos = instrucoes.getApos(seOUloop);
		if (contextoApos != null) {
			processarIfEq(contextoApos);
		} else {
			checarFuncaoSemRetorno(instrucoes);
			expressaoIfEqAcima(instrucoes.getParent());
		}
	}

	protected void expressaoIfEqIf() throws ExpressaoException {
		if (!(parent instanceof IFContexto)) {
			throw new ExpressaoException(ERRO_ESTRUTURA_EXPRESSAO_INVALIDA);
		}
		checarVazioExpressao();
		IFContexto ifContexto = (IFContexto) parent;
		Contexto instrucoes = ifContexto.getParent();
		checarParentInstrucoes(instrucoes);
		Contexto contextoApos = ifContexto.getApos(this);
		if (contextoApos == null) {
			contextoApos = instrucoes.getApos(ifContexto);
		}
		if (contextoApos != null) {
			processarIfEq(contextoApos);
		} else {
			checarFuncaoSemRetorno(instrucoes);
			expressaoIfEqAcima(instrucoes.getParent());
		}
	}

	private void expressaoIfEqAcima(Contexto contexto) throws ExpressaoException {
		if (contexto == null) {
			throw new ExpressaoException("erro.objeto.contexto.nulo");
		}
		if (contexto instanceof IFContexto || contexto instanceof WhileContexto) {
			Contexto instrucoes = contexto.getParent();
			checarParentInstrucoes(instrucoes);
			Contexto contextoApos = instrucoes.getApos(contexto);
			if (contextoApos != null) {
				processarIfEq(contextoApos);
			} else {
				checarFuncaoSemRetorno(instrucoes);
				expressaoIfEqAcima(instrucoes.getParent());
			}
		} else {
			throw new ExpressaoException(ERRO_ESTRUTURA_EXPRESSAO_INVALIDA);
		}
	}

	protected void instrucoesGotoWhile() throws ExpressaoException {
		if (!(parent instanceof WhileContexto)) {
			throw new ExpressaoException("erro.instrucoes.sem_parent", "while");
		}
		checarVazioInstrucoes();
		Contexto ultimo = getUltimo();
		if (ultimo instanceof RetornoContexto) {
			return;
		}
		WhileContexto loop = (WhileContexto) parent;
		Contexto expressao = loop.getPrimeiro();
		if (!(expressao instanceof ExpressaoContexto)) {
			throw new ExpressaoException("erro.loop.sem_expressao");
		}
		processarGoto(expressao);
	}

	protected void instrucoesGotoIf() throws ExpressaoException {
		if (!(parent instanceof IFContexto)) {
			throw new ExpressaoException("erro.instrucoes.sem_parent", "if");
		}
		checarVazioInstrucoes();
		Contexto ultimo = getUltimo();
		if (ultimo instanceof RetornoContexto) {
			return;
		}
		if (ultimo.retornoGarantido()) {
			return;
		}
		IFContexto se = (IFContexto) parent;
		Contexto instrucoes = se.getParent();
		checarParentInstrucoes(instrucoes);
		Contexto contextoApos = instrucoes.getApos(se);
		if (contextoApos != null) {
			processarGoto(contextoApos);
		} else {
			checarFuncaoSemRetorno(instrucoes);
			instrucoesGotoIfAcima(instrucoes.getParent());
		}
	}

	private void instrucoesGotoIfAcima(Contexto contexto) throws ExpressaoException {
		if (contexto == null) {
			throw new ExpressaoException("erro.objeto.contexto.nulo");
		}
		if (contexto instanceof IFContexto || contexto instanceof WhileContexto) {
			Contexto instrucoes = contexto.getParent();
			checarParentInstrucoes(instrucoes);
			Contexto contextoApos = instrucoes.getApos(contexto);
			if (contextoApos != null) {
				processarGoto(contextoApos);
			} else {
				checarFuncaoSemRetorno(instrucoes);
				instrucoesGotoIfAcima(instrucoes.getParent());
			}
		} else {
			throw new ExpressaoException("erro.estrutura.invalida");
		}
	}

	private void processarIfEq(Contexto contexto) throws ExpressaoException {
		contexto.empilharLocalIni();
		List<Contexto> pilha = contexto.getPilhaLocal();
		checarPilha(pilha);
		IFEqContexto ifEqContexto = new IFEqContexto();
		ifEqContexto.setDestino(pilha.get(0));
		adicionar(ifEqContexto);
	}

	private void processarGoto(Contexto contexto) throws ExpressaoException {
		contexto.empilharLocalIni();
		List<Contexto> pilha = contexto.getPilhaLocal();
		checarPilha(pilha);
		GotoContexto gotoContexto = new GotoContexto();
		gotoContexto.setDestino(pilha.get(0));
		adicionar(gotoContexto);
	}
}