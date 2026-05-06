package br.com.persist.plugins.expressao.processador;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.ExpressaoUtil;

public class PilhaFuncao {
	private final List<Funcao> funcoes;

	public PilhaFuncao() {
		funcoes = new ArrayList<>();
	}

	private void checar() throws ExpressaoException {
		if (isEmpty()) {
			throw new ExpressaoException("PilhaFuncao vazia >>> " + toString(), false);
		}
	}

	public void push(Funcao funcao) throws ExpressaoException {
		InstrucaoUtil.checarFuncao(funcao);
		if (ExpressaoConstantes.DEBUG) {
			ExpressaoUtil.print("[PILHA-FUNCAO-PUSH] ", funcao);
		}
		if (funcoes.isEmpty()) {
			funcao.setParent(null);
		} else {
			funcao.setParent(peek());
		}
		funcoes.add(funcao);
	}

	public Funcao peek() throws ExpressaoException {
		checar();
		return funcoes.get(funcoes.size() - 1);
	}

	public Funcao pop() throws ExpressaoException {
		checar();
		Funcao funcao = funcoes.remove(funcoes.size() - 1);
		if (ExpressaoConstantes.DEBUG) {
			ExpressaoUtil.print("[PILHA-FUNCAO-POP] ", funcao);
		}
		return funcao;
	}

	public int size() {
		return funcoes.size();
	}

	public boolean isEmpty() {
		return funcoes.isEmpty();
	}

	public void clear() {
		funcoes.clear();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("PilhaFuncao=");
		if (funcoes.isEmpty()) {
			builder.append("<<<empty>>>");
		} else {
			builder.append(ExpressaoUtil.toString(funcoes));
		}
		return builder.toString();
	}
}