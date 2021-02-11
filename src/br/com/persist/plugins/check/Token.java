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

	public static List<Token> criarTokens(String string) {
		List<Token> lista = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			sb.append(c);
			if (c == '(') {
				lista.add(new TokenMetodoIni(i, sb.toString()));
				limpar(sb);
			} else if (c == ')') {
				lista.add(new TokenMetodoFim(i, sb.toString()));
				limpar(sb);
			} else if (c == ',') {
				lista.add(new TokenParam(i, sb.toString()));
				limpar(sb);
			}
		}
		return lista;
	}

	private static void limpar(StringBuilder sb) {
		if (sb.length() > 0) {
			sb.delete(0, sb.length());
		}
	}
}