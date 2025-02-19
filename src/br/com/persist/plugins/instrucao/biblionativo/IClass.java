package br.com.persist.plugins.instrucao.biblionativo;

public class IClass {
	private IClass() {
	}

	@Biblio(1)
	public static Object get(Object absoluto) throws ClassNotFoundException {
		return Class.forName((String) absoluto);
	}

	@Biblio(2)
	public static Object getType(Object objeto) {
		if (objeto == null) {
			return "null";
		}
		return objeto.getClass().getName();
	}
}