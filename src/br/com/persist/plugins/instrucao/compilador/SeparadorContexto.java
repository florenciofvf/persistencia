package br.com.persist.plugins.instrucao.compilador;

public class SeparadorContexto extends Container {
	public SeparadorContexto(Token token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return token.getString();
	}
}