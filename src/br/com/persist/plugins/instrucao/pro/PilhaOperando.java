package br.com.persist.plugins.instrucao.pro;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.inst.InstrucaoUtil;

public class PilhaOperando {
	private final List<Object> operandos;

	public PilhaOperando() {
		operandos = new ArrayList<>();
	}

	private void checar() throws InstrucaoException {
		if (isEmpty()) {
			throw new InstrucaoException("PilhaOperando vazia >>> " + toString(), false);
		}
	}

	public Object peek() throws InstrucaoException {
		checar();
		return operandos.get(operandos.size() - 1);
	}

	public void push(Object valor) throws InstrucaoException {
		InstrucaoUtil.checarOperando(valor);
		operandos.add(valor);
	}

	public Object pop() throws InstrucaoException {
		checar();
		return operandos.remove(operandos.size() - 1);
	}

	public int size() {
		return operandos.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public String toString() {
		return "PilhaOperando=" + operandos.toString();
	}

	public void clear() {
		operandos.clear();
	}
}