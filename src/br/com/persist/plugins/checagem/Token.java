package br.com.persist.plugins.checagem;

public class Token {
	public static final int PARENTESE_ABRIR = 1;
	public static final int PARENTESE_FECHA = 2;
	public static final int VIRGULA = 3;
	public static final int BOOLEAN = 4;
	public static final int STRING = 5;
	public static final int DOUBLE = 6;
	public static final int LONG = 7;

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
		return tipo == BOOLEAN;
	}

	public boolean isString() {
		return tipo == STRING;
	}

	public boolean isDouble() {
		return tipo == DOUBLE;
	}

	public boolean isLong() {
		return tipo == LONG;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(valor);
		switch (tipo) {
		case PARENTESE_ABRIR:
			sb.append(", PARENTESE_ABRIR");
			break;
		case PARENTESE_FECHA:
			sb.append(", PARENTESE_FECHA");
			break;
		case VIRGULA:
			sb.append(", VIRGULA");
			break;
		case BOOLEAN:
			sb.append(", BOOLEAN");
			break;
		case STRING:
			sb.append(", STRING");
			break;
		case DOUBLE:
			sb.append(", DOUBLE");
			break;
		case LONG:
			sb.append(", LONG");
			break;
		default:
			sb.append(", INVALIDO");
		}
		return sb.toString();
	}
}