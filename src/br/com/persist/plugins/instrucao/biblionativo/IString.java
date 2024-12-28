package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;

public class IString {
	private IString() {
	}

	@Biblio(1)
	public static StringPool createStringPool() {
		return new StringPool();
	}

	@Biblio(5)
	public static void append(Object stringPool, Object object) {
		if (stringPool instanceof StringPool) {
			((StringPool) stringPool).append(object);
		}
	}

	@Biblio(8)
	public static BigInteger empty(Object object) {
		if (object == null) {
			return IUtil.TRUE;
		}
		String string = object.toString();
		if (string == null) {
			return IUtil.TRUE;
		}
		return string.trim().isEmpty() ? IUtil.TRUE : IUtil.FALSE;
	}

	@Biblio(2)
	public static BigInteger notEmpty(Object object) {
		return empty(object).equals(IUtil.FALSE) ? IUtil.TRUE : IUtil.FALSE;
	}

	@Biblio(6)
	public static String trim(Object object) {
		return object == null ? "" : object.toString().trim();
	}

	@Biblio(3)
	public static String upper(Object object) {
		return object == null ? "" : object.toString().toUpperCase();
	}

	@Biblio(4)
	public static String lower(Object object) {
		return object == null ? "" : object.toString().toLowerCase();
	}

	@Biblio(7)
	public static BigInteger size(Object object) {
		return object == null ? BigInteger.ZERO : BigInteger.valueOf(object.toString().length());
	}
}