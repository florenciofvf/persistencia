package br.com.persist.plugins.check;

public class TokenParam extends Token {

	public TokenParam(int indice, String string) {
		super(indice, string.substring(0, string.length() - 1));
	}

	public String getStringTrim() {
		return string.trim();
	}
}