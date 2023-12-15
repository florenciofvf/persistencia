package br.com.persist.plugins.atributo;

import java.util.Objects;

import br.com.persist.assistencia.Util;

public class AtributoProcessador {
	private final AtributoHandler handler;
	private final String string;
	private int indice;

	public AtributoProcessador(AtributoHandler handler, String string) {
		this.handler = Objects.requireNonNull(handler);
		this.string = Objects.requireNonNull(string);
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

	private Token proximoToken() {
		if (indice >= string.length()) {
			return null;
		}
		pularDescartaveis();
		if (indice >= string.length()) {
			return null;
		}
		return proximoTokenImpl();
	}

	private Token proximoTokenImpl() {
		char c = string.charAt(indice);
		switch (c) {
		case '"':
			indice++;
			return tokenString();
		case ':':
		case '{':
		case '}':
			indice++;
			return new Token(c);
		default:
			throw new IllegalStateException(c + " >>> " + indice);
		}
	}

	private Token tokenString() {
		StringBuilder sb = new StringBuilder();
		boolean encerrado = false;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c == '\"') {
				encerrado = true;
				break;
			} else {
				sb.append(c);
			}
			indice++;
		}
		if (!encerrado) {
			throw new IllegalStateException(indice + " <<< String nao encerrada >>> " + sb.toString());
		}
		indice++;
		String str = sb.toString();
		if (Util.isEmpty(str)) {
			throw new IllegalStateException("String vazia >>> " + indice);
		}
		return new Token(str);
	}

	public void processar() {
		Token token = proximoToken();
		while (token != null) {
			if (":".equals(token.string)) {
				handler.separador();
			} else if ("{".equals(token.string)) {
				handler.iniMapa();
			} else if ("}".equals(token.string)) {
				handler.fimMapa();
			} else {
				handler.setString(token.string);
			}
			token = proximoToken();
		}
	}
}

class Token {
	final String string;

	public Token(String string) {
		this.string = string;
	}

	public Token(char c) {
		this("" + c);
	}

	boolean isEmpty() {
		return Util.isEmpty(string);
	}
}