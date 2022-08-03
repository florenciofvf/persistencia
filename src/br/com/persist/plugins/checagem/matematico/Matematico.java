package br.com.persist.plugins.checagem.matematico;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.ChecagemNumero;
import br.com.persist.plugins.checagem.funcao.FuncaoBinariaInfixa;

public abstract class Matematico extends FuncaoBinariaInfixa implements ChecagemNumero {
	protected Object concatenar(Object pri, Object seg) {
		if (pri != null && seg != null) {
			return pri.toString() + seg.toString();
		} else if (pri != null) {
			return pri.toString() + "null";
		} else if (seg != null) {
			return "null" + seg.toString();
		}
		return "nullnull";
	}

	protected void checkOperandoDiv(long l) throws ChecagemException {
		if (l == 0) {
			throw new ChecagemException(getClass(), "Nao existe divisao por zero");
		}
	}

	protected void checkOperandoDiv(double d) throws ChecagemException {
		checkOperandoDiv((long) d);
	}

	protected void checkOperandoDiv(BigInteger bigInteger) throws ChecagemException {
		checkOperandoDiv(bigInteger.longValue());
	}

	protected void checkOperandoDiv(BigDecimal bigDecimal) throws ChecagemException {
		checkOperandoDiv(bigDecimal.longValue());
	}
}