package br.com.persist.plugins.instrucao.biblio_nativo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Util {
	private Util() {
	}

	private static BigInteger createFalse() {
		return BigInteger.valueOf(0);
	}

	private static BigInteger createTrue() {
		return BigInteger.valueOf(1);
	}

	public static BigInteger isNull(Object object) {
		return object == null ? createTrue() : createFalse();
	}

	public static BigInteger isNotNull(Object object) {
		return object != null ? createTrue() : createFalse();
	}

	public static BigInteger stringNotEmpty(Object object) {
		BigInteger respo = stringEmpty(object);
		BigInteger falso = createFalse();
		BigInteger verda = createTrue();
		return respo.equals(verda) ? falso : verda;
	}

	public static BigInteger stringEmpty(Object object) {
		if (object == null) {
			return createTrue();
		}
		String string = object.toString();
		if (string == null) {
			return createTrue();
		}
		return string.trim().length() > 0 ? createFalse() : createTrue();
	}

	public static BigInteger parseBigInteger(Object object) {
		return new BigInteger(object.toString());
	}

	public static BigDecimal parseBigDecimal(Object object) {
		return new BigDecimal(object.toString());
	}

	public static String toString(Object object) {
		return object.toString();
	}
}