package br.com.persist.assistencia;

public class FragmentoUtil {
	private int indice;
	private final String string;

	public FragmentoUtil(String string) {
		this.string = string.trim();
	}

	public String proximo() {
		StringBuilder sb = new StringBuilder();
		while (indice < string.length()) {
			char c = string.charAt(indice);
			sb.append(c);
			if (c == '\n') {
				indice++;
				avancar(sb);
				break;
			}
			indice++;
		}
		return sb.toString();
	}

	private void avancar(StringBuilder sb) {
		while (indice < string.length()) {
			char c = string.charAt(indice);
			sb.append(c);
			if (c == '\n') {
				break;
			}
			indice++;
		}
	}
}