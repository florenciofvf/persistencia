package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;

public class Util {
	public static final BigInteger FALSE = BigInteger.valueOf(0);
	public static final BigInteger TRUE = BigInteger.valueOf(1);

	private Util() {
	}

	public static BigInteger getFalse() {
		return FALSE;
	}

	public static BigInteger getTrue() {
		return TRUE;
	}

	public static BigInteger isNull(Object object) {
		return object == null ? TRUE : FALSE;
	}

	public static BigInteger isNotNull(Object object) {
		return object != null ? TRUE : FALSE;
	}

	public static java.lang.String toString(Object object) {
		return object == null ? "" : object.toString();
	}
}