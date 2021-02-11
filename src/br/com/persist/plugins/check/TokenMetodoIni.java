package br.com.persist.plugins.check;

public class TokenMetodoIni extends Token {

	public TokenMetodoIni(int indice, String string) {
		super(indice, string.substring(0, string.length()));
	}

	@Override
	public String getString() {
		return string.trim();
	}
}