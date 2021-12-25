package br.com.persist.plugins.requisicao;

public class Fragmento {
	private int indice;
	private final String string;

	public Fragmento(String string) {
		this.string = string.trim();
	}

	public String proximo() {
		StringBuilder sb = new StringBuilder();
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c != '\n') {
				sb.append(c);
				indice++;
			} else {
				if (indice + 1 < string.length()) {
					char d = string.charAt(indice + 1);
					if (d == '\n') {
						indice++;
						break;
					}
					indice++;
				}
			}
		}
		return sb.toString();
	}
}