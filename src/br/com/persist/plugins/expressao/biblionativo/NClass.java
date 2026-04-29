package br.com.persist.plugins.expressao.biblionativo;

public class NClass {
	private NClass() {
	}

	@Biblio(1)
	public static Object forName(Object absoluto) throws ClassNotFoundException {
		return Class.forName((String) absoluto);
	}

	@Biblio(2)
	public static Object nameAbsolute(Object objeto) {
		if (objeto == null) {
			return "null";
		}
		return objeto.getClass().getName();
	}
}