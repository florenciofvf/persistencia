package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;

public class String {
	private String() {
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

	public static java.lang.String trim(Object object) {
		return object == null ? "" : object.toString().trim();
	}
}