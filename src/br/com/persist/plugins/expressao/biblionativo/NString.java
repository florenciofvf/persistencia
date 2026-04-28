package br.com.persist.plugins.expressao.biblionativo;

public class NString {
	private NString() {
	}

	@Biblio(1)
	public static String lower(Object object) {
		return object == null ? "" : object.toString().toLowerCase();
	}

	@Biblio(2)
	public static String upper(Object object) {
		return object == null ? "" : object.toString().toUpperCase();
	}
}