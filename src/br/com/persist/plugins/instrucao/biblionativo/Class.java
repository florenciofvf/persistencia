package br.com.persist.plugins.instrucao.biblionativo;

public class Class {
	private Class() {
	}

	@Biblio
	public static Object get(Object absoluto) throws ClassNotFoundException {
		return java.lang.Class.forName((String) absoluto);
	}
}