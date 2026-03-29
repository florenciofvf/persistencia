package br.com.persist.plugins.expressao.compilador;

import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;

public class InstrucoesContexto extends Contexto {
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
		checarVazio();
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
			throw new ExpressaoException("erro.pilhaLocal.vazio");
		}
		gotoContexto = new GotoContexto();
		gotoContexto.setDestino(pilha.get(0));
		add(gotoContexto);
	}

	private void configurarSaltoSe() throws ExpressaoException {
		if (!(parent instanceof IFContexto)) {
			throw new ExpressaoException("erro.instrucoes.sem_parent", "if");
		}
		checarVazio();
		Contexto ultimo = getUltimo();
		if (ultimo instanceof RetornoContexto) {
			return;
		}
		IFContexto se = (IFContexto) parent;
	}
}