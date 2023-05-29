package br.com.persist.plugins.instrucao;

import java.util.ArrayList;
import java.util.List;

public class PilhaOperando {
	private final List<Object> operandos;

	public PilhaOperando() {
		operandos = new ArrayList<>();
	}

	public Object ref() {
		return operandos.get(operandos.size() - 1);
	}

	public void add(Object valor) throws InstrucaoException {
		InstrucaoUtil.checarOperando(valor);
		operandos.add(valor);
	}

	public Object remove() {
		return operandos.remove(operandos.size() - 1);
	}
}