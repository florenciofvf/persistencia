package br.com.persist.plugins.checagem;

public class ChecagemToken {
	private final String string;
	private int indice;

	public ChecagemToken(String string) {
		this.string = string.trim();
		indice = 0;
	}

	public Token proximoToken() {
		if (indice >= string.length()) {
			return null;
		}
		pularDescartaveis();
		return null;
	}

	private void pularDescartaveis() {
		// TODO - finalizar
	}
}