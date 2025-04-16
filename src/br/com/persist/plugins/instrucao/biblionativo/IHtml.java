package br.com.persist.plugins.instrucao.biblionativo;

public class IHtml {
	private static final String FIM_FONT = "</font>";

	private IHtml() {
	}

	@Biblio(0)
	public static String erro(Object object) {
		if (object == null) {
			object = "";
		}
		return iniFont("red") + object.toString() + fimFont();
	}

	@Biblio(1)
	public static String info(Object object) {
		if (object == null) {
			object = "";
		}
		return iniFont("blue") + object.toString() + fimFont();
	}

	@Biblio(2)
	public static String warn(Object object) {
		if (object == null) {
			object = "";
		}
		return iniFont("orange") + object.toString() + fimFont();
	}

	private static String iniFont(String cor) {
		return "<font color=\"" + cor + "\">";
	}

	private static String fimFont() {
		return FIM_FONT;
	}
}