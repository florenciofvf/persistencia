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
}