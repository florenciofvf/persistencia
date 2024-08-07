package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;

public class String {
	private String() {
	}

	@Biblio
	public static StringPool createStringPool() {
		return new StringPool();
	}

	@Biblio
	public static void append(StringPool stringPool, Object object) {
		if (stringPool != null) {
			stringPool.append(object);
		}
	}

	@Biblio
	public static BigInteger empty(Object object) {
		if (object == null) {
			return Util.TRUE;
		}
		java.lang.String string = object.toString();
		if (string == null) {
			return Util.TRUE;
		}
		return string.trim().isEmpty() ? Util.TRUE : Util.FALSE;
	}

	@Biblio
	public static BigInteger notEmpty(Object object) {
		return empty(object).equals(Util.FALSE) ? Util.TRUE : Util.FALSE;
	}

	@Biblio
	public static java.lang.String trim(Object object) {
		return object == null ? "" : object.toString().trim();
	}

	@Biblio
	public static java.lang.String upper(Object object) {
		return object == null ? "" : object.toString().toUpperCase();
	}

	@Biblio
	public static java.lang.String lower(Object object) {
		return object == null ? "" : object.toString().toLowerCase();
	}

	@Biblio
	public static BigInteger size(Object object) {
		return object == null ? BigInteger.ZERO : BigInteger.valueOf(object.toString().length());
	}
}