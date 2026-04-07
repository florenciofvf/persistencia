package br.com.persist.plugins.expressao.biblionativo;

public class StringUtil {
	private StringUtil() {
	}

	public static String upper(Object object) {
		return object == null ? "" : object.toString().toUpperCase();
	}
}