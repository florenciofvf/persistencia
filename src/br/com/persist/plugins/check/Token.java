package br.com.persist.plugins.check;

import java.util.ArrayList;
import java.util.List;

public abstract class Token {
	protected final String string;
	protected final int indice;

	public Token(int indice, String string) {
		this.indice = indice;
		this.string = string;
	}

	public String getString() {
		return string;
	}

	@Override
	public String toString() {
		return string;
	}

	public static List<Token> criarTokens(String string) {
		List<Token> lista = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		int i = 0;
		while (i < string.length()) {
			char c = string.charAt(i);
			sb.append(c);
			if (c == '(') {
				lista.add(new TokenMetodoIni(i, sb.toString()));
				limpar(sb);
			} else if (c == ')') {
				lista.add(new TokenMetodoFim(i, sb.toString()));
				i += avancar(i + 1, string);
				limpar(sb);
			} else if (c == ',') {
				lista.add(new TokenParam(i, sb.toString()));
				limpar(sb);
			}
			i++;
		}
		return lista;
	}

	private static int avancar(int indice, String string) {
		int total = 0;
		for (int i = indice; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c <= ' ') {
				total++;
			} else {
				break;
			}
		}
		return total;
	}

	private static void limpar(StringBuilder sb) {
		if (sb.length() > 0) {
			sb.delete(0, sb.length());
		}
	}
}