package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Parse {
	private Parse() {
	}

	public static BigInteger bigInteger(Object object) {
		return new BigInteger(object.toString());
	}

	public static BigDecimal bigDecimal(Object object) {
		return new BigDecimal(object.toString());
	}
}