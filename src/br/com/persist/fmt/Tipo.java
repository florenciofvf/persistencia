package br.com.persist.fmt;

public class Tipo {

	public void toString(StringBuilder sb, boolean comTab, int tab) {
		if (comTab) {
			sb.append(getTab(tab));
		}
	}

	public static String getTab(int i) {
		StringBuilder sb = new StringBuilder();

		int q = 0;

		while (q < i) {
			sb.append("    ");
			q++;
		}

		return sb.toString();
	}

	public static String citar(String s) {
		return "\"" + s + "\"";
	}
}