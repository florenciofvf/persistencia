package br.com.persist.plugins.checagem.matematico;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.persist.plugins.checagem.FuncaoBinaria;

public abstract class Matematico extends FuncaoBinaria {

	public boolean isNumericoValido(Object obj) {
		return obj instanceof Short || obj instanceof Integer || obj instanceof Long || obj instanceof Float
				|| obj instanceof Double || obj instanceof BigInteger || obj instanceof BigDecimal;
	}

	Short getShort(Object o) {
		return (Short) o;
	}

	Integer getInteger(Object o) {
		return (Integer) o;
	}

	Long getLong(Object o) {
		return (Long) o;
	}

	Float getFloat(Object o) {
		return (Float) o;
	}

	Double getDouble(Object o) {
		return (Double) o;
	}
}