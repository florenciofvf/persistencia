package br.com.persist.plugins.expressao.processador;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;

public class PilhaOperando {
	private final List<Object> operandos;

	public PilhaOperando() {
		operandos = new ArrayList<>();
	}

	private void checar() throws ExpressaoException {
		if (isEmpty()) {
			throw new ExpressaoException("PilhaOperando vazia >>> " + toString(), false);
		}
	}

	public void push(Object valor) throws ExpressaoException {
		InstrucaoUtil.checarOperando(valor);
		operandos.add(valor);
	}

	public Object peek() throws ExpressaoException {
		checar();
		return operandos.get(operandos.size() - 1);
	}

	public Object pop() throws ExpressaoException {
		checar();
		return operandos.remove(operandos.size() - 1);
	}

	public int size() {
		return operandos.size();
	}

	public boolean isEmpty() {
		return operandos.isEmpty();
	}

	public void clear() {
		operandos.clear();
	}

	public void setArgumentos(Funcao funcao) throws ExpressaoException {
		List<Integer> indices = funcao.getIndiceParametros();
		for (int i = indices.size() - 1; i >= 0; i--) {
			funcao.setValorParametro(indices.get(i), pop());
		}
	}

	@Override
	public String toString() {
		return "PilhaOperando=" + operandos.toString();
	}
}