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

	@Biblio(14)
	public static void ordenar() {
		textPool.ordenar();
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

	@Biblio(8)
	public static void noneUnderline(Object object) {
		if (object != null) {
			textPool.noneUnderline(object.toString());
		}
	}

	@Biblio(9)
	public static void infoUnderline(Object object) {
		if (object != null) {
			textPool.infoUnderline(object.toString());
		}
	}

	@Biblio(10)
	public static void warnUnderline(Object object) {
		if (object != null) {
			textPool.warnUnderline(object.toString());
		}
	}

	@Biblio(11)
	public static void erroUnderline(Object object) {
		if (object != null) {
			textPool.erroUnderline(object.toString());
		}
	}

	@Biblio(12)
	public static void notaUnderline(Object object) {
		if (object != null) {
			textPool.notaUnderline(object.toString());
		}
	}

	@Biblio(13)
	public static void showUnderline(Object object) {
		if (object != null) {
			textPool.showUnderline(object.toString());
		}
	}
}