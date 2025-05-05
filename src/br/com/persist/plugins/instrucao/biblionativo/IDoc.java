package br.com.persist.plugins.instrucao.biblionativo;

import br.com.persist.assistencia.TextPool;

public class IDoc {
	private static TextPool textPool = new TextPool();

	private IDoc() {
	}

	@Biblio(0)
	public static void init() {
		textPool.init();
	}

	@Biblio(1)
	public static TextPool getPool() {
		return textPool;
	}

	@Biblio(8)
	public static void noneUnderline(Object object) {
		if (object != null) {
			textPool.noneUnderline(object.toString());
		}
	}

	@Biblio(2)
	public static void none(Object object) {
		if (object != null) {
			textPool.none(object.toString());
		}
	}

	@Biblio(3)
	public static void info(Object object) {
		if (object != null) {
			textPool.info(object.toString());
		}
	}

	@Biblio(4)
	public static void warn(Object object) {
		if (object != null) {
			textPool.warn(object.toString());
		}
	}

	@Biblio(5)
	public static void erro(Object object) {
		if (object != null) {
			textPool.erro(object.toString());
		}
	}

	@Biblio(6)
	public static void nota(Object object) {
		if (object != null) {
			textPool.nota(object.toString());
		}
	}

	@Biblio(7)
	public static void show(Object object) {
		if (object != null) {
			textPool.show(object.toString());
		}
	}
}