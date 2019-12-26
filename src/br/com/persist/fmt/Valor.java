package br.com.persist.fmt;

public abstract class Valor {
	private final String tipo;

	public Valor(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public abstract void fmt(StringBuilder sb, int tab);

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