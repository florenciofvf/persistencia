package br.com.persist.assistencia;

public class FragmentoUtil {
	private int indice;
	private final String string;

	public FragmentoUtil(String string, int indice) {
		this.string = string.trim();
		this.indice = indice;
	}

	public FragmentoUtil(String string) {
		this(string, 0);
	}

	public String proximo() {
		StringBuilder sb = new StringBuilder();
		while (indice < string.length()) {
			char c = string.charAt(indice);
			sb.append(c);
			if (c == '\n' && finalFragmento(indice + 1)) {
				indice++;
				break;
			}
			indice++;
		}
		return sb.toString();
	}

	private boolean finalFragmento(int indice) {
		StringBuilder sb = new StringBuilder();
		while (indice < string.length()) {
			char c = string.charAt(indice);
			sb.append(c);
			if (c == '\n') {
				break;
			}
			indice++;
		}
		return sb.toString().trim().isEmpty();
	}
}