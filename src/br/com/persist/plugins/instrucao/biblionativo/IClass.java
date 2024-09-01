package br.com.persist.plugins.instrucao.biblionativo;

public class IClass {
	private IClass() {
	}

	@Biblio
	public static Object get(Object absoluto) throws ClassNotFoundException {
		return Class.forName((String) absoluto);
	}
}