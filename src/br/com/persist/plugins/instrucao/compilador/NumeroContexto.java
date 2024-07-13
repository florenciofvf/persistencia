package br.com.persist.plugins.instrucao.compilador;

public class NumeroContexto extends Container {
	private final String numero;

	public NumeroContexto(Token token) {
		this.numero = token.getString();
		this.token = token;
	}

	public String getNumero() {
		return numero;
	}

	@Override
	public String toString() {
		return numero;
	}
}