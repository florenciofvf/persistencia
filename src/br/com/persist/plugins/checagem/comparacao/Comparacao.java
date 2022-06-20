package br.com.persist.plugins.checagem.comparacao;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.persist.plugins.checagem.FuncaoBinaria;

public abstract class Comparacao extends FuncaoBinaria {

	public boolean isNumericoValido(Object obj) {
		return obj instanceof Short || obj instanceof Integer || obj instanceof Long || obj instanceof Float
				|| obj instanceof Double || obj instanceof BigInteger || obj instanceof BigDecimal;
	}

	short getShort(Object o) {
		return (Short) o;
	}

	int getInteger(Object o) {
		return (Integer) o;
	}

	long getLong(Object o) {
		return (Long) o;
	}

	float getFloat(Object o) {
		return (Float) o;
	}

	double getDouble(Object o) {
		return (Double) o;
	}

	boolean getBoolean(Object o) {
		return (Boolean) o;
	}

	String getString(Object o) {
		return (String) o;
	}
}