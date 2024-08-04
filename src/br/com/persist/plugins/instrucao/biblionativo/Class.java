package br.com.persist.plugins.instrucao.biblionativo;

public class Class {
	private Class() {
	}

	@Biblio
	public static Object get(Object absoluto) {
		try {
			return java.lang.Class.forName((java.lang.String) absoluto);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
}