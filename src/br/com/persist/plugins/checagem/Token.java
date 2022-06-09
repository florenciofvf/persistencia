package br.com.persist.plugins.checagem;

public class Token {
	public static final int TIPO_BOOLEAN = 1;
	public static final int TIPO_STRING = 2;
	public static final int TIPO_DOUBLE = 3;
	public static final int TIPO_LONG = 4;
	private final String valor;
	private final int tipo;

	public Token(String valor, int tipo) {
		this.valor = valor;
		this.tipo = tipo;
	}

	public String getValor() {
		return valor;
	}

	public int getTipo() {
		return tipo;
	}

	public boolean isBoolean() {
		return tipo == TIPO_BOOLEAN;
	}

	public boolean isString() {
		return tipo == TIPO_STRING;
	}

	public boolean isDouble() {
		return tipo == TIPO_DOUBLE;
	}

	public boolean isLong() {
		return tipo == TIPO_LONG;
	}
}