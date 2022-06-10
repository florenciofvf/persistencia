package br.com.persist.plugins.checagem;

public class ChecagemToken {
	private final String string;
	private int indice;

	public ChecagemToken(String string) {
		this.string = string.trim();
		indice = 0;
	}

	private void pularDescartaveis() {
		while (indice < string.length()) {
			if (string.charAt(indice) <= ' ') {
				indice++;
			} else {
				break;
			}
		}
	}

	public Token proximoToken() {
		if (indice >= string.length()) {
			return null;
		}
		pularDescartaveis();
		if (indice >= string.length()) {
			return null;
		}
		char c = string.charAt(indice);
		switch (c) {
		case '\'':
			return tokenString();
		case '(':
			indice++;
			return new Token("" + c, Token.PARENTESE_ABRIR);
		case ')':
			indice++;
			return new Token("" + c, Token.PARENTESE_FECHA);
		case ',':
			indice++;
			return new Token("" + c, Token.VIRGULA);
		default:
			return token();
		}
	}

	private Token tokenString() {
		StringBuilder sb = new StringBuilder();
		boolean escapeAtivado = false;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c == '\'') {
				if (escapeAtivado) {
					sb.append(c);
					escapeAtivado = false;
				} else {
					break;
				}
			} else if (c == '\\') {
				escapeAtivado = true;
			} else {
				sb.append(c);
				escapeAtivado = false;
			}
			indice++;
		}
		indice++;
		return new Token(sb.toString(), Token.STRING);
	}

	private Token token() {
		StringBuilder sb = new StringBuilder();
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c == '\'' || c == '(' || c == ')' || c == ',') {
				break;
			} else {
				sb.append(c);
			}
			indice++;
		}
		return new Token(sb.toString(), Token.STRING);
	}
}