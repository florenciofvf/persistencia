package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;

public class IString {
	private IString() {
	}

	@Biblio
	public static StringPool createStringPool() {
		return new StringPool();
	}

	@Biblio
	public static void append(Object stringPool, Object object) {
		if (stringPool instanceof StringPool) {
			((StringPool) stringPool).append(object);
		}
	}

	@Biblio
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

	@Biblio
	public static BigInteger notEmpty(Object object) {
		return empty(object).equals(IUtil.FALSE) ? IUtil.TRUE : IUtil.FALSE;
	}

	@Biblio
	public static String trim(Object object) {
		return object == null ? "" : object.toString().trim();
	}

	@Biblio
	public static String upper(Object object) {
		return object == null ? "" : object.toString().toUpperCase();
	}

	@Biblio
	public static String lower(Object object) {
		return object == null ? "" : object.toString().toLowerCase();
	}

	@Biblio
	public static BigInteger size(Object object) {
		return object == null ? BigInteger.ZERO : BigInteger.valueOf(object.toString().length());
	}
}