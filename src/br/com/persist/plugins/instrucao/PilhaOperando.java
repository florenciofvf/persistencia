package br.com.persist.plugins.instrucao;

import java.math.BigDecimal;
import java.math.BigInteger;
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
		if (valido(valor)) {
			operandos.add(valor);
		} else {
			throw new InstrucaoException("erro.valor_invalido_operando", valor);
		}
	}

	public Object remove() {
		return operandos.remove(operandos.size() - 1);
	}

	static boolean valido(Object valor) {
		return (valor instanceof String) || (valor instanceof BigInteger) || (valor instanceof BigDecimal);
	}
}