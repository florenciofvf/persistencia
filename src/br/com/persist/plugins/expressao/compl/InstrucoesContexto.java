package br.com.persist.plugins.expressao.compl;

import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;

public class InstrucoesContexto extends Contexto {
	private static final String ERRO_PILHA_LOCAL_VAZIO = "erro.pilhaLocal.vazio";
	public static final byte FUNCAO = 0;
	public static final byte LOOP = 1;
	private GotoContexto gotoContexto;
	public static final byte SE = 2;
	private final byte estrutura;

	public InstrucoesContexto(byte estrutura) {
		super();
		this.estrutura = estrutura;
	}

	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		if (token.isReservado()) {
			if (ExpressaoConstantes.CONST.equals(token.getString())) {
				ConstanteContexto constante = new ConstanteContexto();
				compilador.setSelecionado(constante);
				add(constante);
			} else if (ExpressaoConstantes.RETURN.equals(token.getString())) {
				RetornoContexto retorno = new RetornoContexto();
				compilador.setSelecionado(retorno);
				add(retorno);
			} else if (ExpressaoConstantes.IF.equals(token.getString())) {
				IFContexto se = new IFContexto();
				compilador.setSelecionado(se);
				add(se);
			} else if (ExpressaoConstantes.WHILE.equals(token.getString())) {
				WhileContexto loop = new WhileContexto();
				compilador.setSelecionado(loop);
				add(loop);
			} else {
				compilador.invalidar(token);
			}
		} else if (token.isChave() || token.isChave2()) {
			InvocacaoContexto invocacao = new InvocacaoContexto(token);
			compilador.setSelecionado(invocacao);
			add(invocacao);
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	protected void configurarSaltosPos() throws ExpressaoException {
		if (estrutura == LOOP) {
			configurarSaltoLoop();
		} else if (estrutura == SE) {
			configurarSaltoSe();
		}
	}

	private void configurarSaltoLoop() throws ExpressaoException {
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
		expressao.empilharLocalIni();
		List<Contexto> pilha = expressao.getPilhaLocal();
		if (pilha.isEmpty()) {
			throw new ExpressaoException(ERRO_PILHA_LOCAL_VAZIO);
		}
		gotoContexto = new GotoContexto();
		gotoContexto.setDestino(pilha.get(0));
		add(gotoContexto);
	}

	private void configurarSaltoSe() throws ExpressaoException {
		if (!(parent instanceof IFContexto)) {
			throw new ExpressaoException("erro.instrucoes.sem_parent", "if");
		}
		checarVazioInstrucoes();
		Contexto ultimo = getUltimo();
		if (ultimo instanceof RetornoContexto) {
			return;
		}
		IFContexto se = (IFContexto) parent;
		Contexto instrucoes = se.parent;
		if (!(instrucoes instanceof InstrucoesContexto)) {
			throw new ExpressaoException("erro.if.sem_parent");
		}
		Contexto contextoApos = instrucoes.getApos(se);
		if (contextoApos != null) {
			contextoApos.empilharLocalIni();
			List<Contexto> pilha = contextoApos.getPilhaLocal();
			if (pilha.isEmpty()) {
				throw new ExpressaoException(ERRO_PILHA_LOCAL_VAZIO);
			}
			gotoContexto = new GotoContexto();
			gotoContexto.setDestino(pilha.get(0));
			add(gotoContexto);
		} else {
			if (instrucoes.parent instanceof FuncaoContexto) {
				throw new ExpressaoException("erro.funcao.sem_retorno");
			}
			configurarSaltoSeAcima(instrucoes.parent);
		}
	}

	private void configurarSaltoSeAcima(Contexto contexto) throws ExpressaoException {
		if (contexto == null) {
			throw new ExpressaoException("erro.objeto.contexto.nulo");
		}
		if (contexto instanceof IFContexto || contexto instanceof WhileContexto) {
			Contexto instrucoes = contexto.parent;
			if (!(instrucoes instanceof InstrucoesContexto)) {
				throw new ExpressaoException("erro.estrutura.sem_parent", contexto.getClass().getName());
			}
			Contexto contextoApos = instrucoes.getApos(contexto);
			if (contextoApos != null) {
				contextoApos.empilharLocalIni();
				List<Contexto> pilha = contextoApos.getPilhaLocal();
				if (pilha.isEmpty()) {
					throw new ExpressaoException(ERRO_PILHA_LOCAL_VAZIO);
				}
				gotoContexto = new GotoContexto();
				gotoContexto.setDestino(pilha.get(0));
				add(gotoContexto);
			} else {
				if (instrucoes.parent instanceof FuncaoContexto) {
					throw new ExpressaoException("erro.funcao.sem_retorno");
				}
				configurarSaltoSeAcima(instrucoes.parent);
			}
		} else {
			throw new ExpressaoException("erro.estrutura.invalida");
		}
	}
}