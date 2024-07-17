package br.com.persist.plugins.instrucao.compilador;

public class NegativoContexto extends Container {
	public static final String NEG = "neg";

	public NegativoContexto(Token token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return token.getString();
	}
}