package br.com.persist.plugins.check;

public class TokenMetodoFim extends Token {

	public TokenMetodoFim(int indice, String string) {
		super(indice, string.substring(0, string.length()));
	}
}