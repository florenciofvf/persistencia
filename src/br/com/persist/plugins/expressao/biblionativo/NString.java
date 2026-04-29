package br.com.persist.plugins.expressao.biblionativo;

import java.math.BigInteger;

public class NString {
	private NString() {
	}

	@Biblio(1)
	public static StringPool createStringPool() {
		return new StringPool();
	}

	@Biblio(2)
	public static void append(Object stringPool, Object object) {
		if (stringPool instanceof StringPool) {
			((StringPool) stringPool).append(object);
		}
	}

	@Biblio(3)
	public static BigInteger empty(Object object) {
		if (object == null) {
			return NUtil.TRUE;
		}
		String string = object.toString();
		if (string == null) {
			return NUtil.TRUE;
		}
		return string.trim().isEmpty() ? NUtil.TRUE : NUtil.FALSE;
	}

	@Biblio(4)
	public static String trim(Object object) {
		return object == null ? "" : object.toString().trim();
	}

	@Biblio(5)
	public static String lower(Object object) {
		return object == null ? "" : object.toString().toLowerCase();
	}

	@Biblio(6)
	public static String upper(Object object) {
		return object == null ? "" : object.toString().toUpperCase();
	}

	@Biblio(7)
	public static BigInteger size(Object object) {
		return object == null ? BigInteger.ZERO : BigInteger.valueOf(object.toString().length());
	}

	@Biblio(8)
	public static BigInteger contains(Object string, Object procurado) {
		if (string == null || procurado == null) {
			return NUtil.FALSE;
		}
		return string.toString().contains(procurado.toString()) ? NUtil.TRUE : NUtil.FALSE;
	}
}