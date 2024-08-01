package br.com.persist.plugins.instrucao.compilador;

public class Token {
	final String string;
	final Tipo tipo;
	int indice2;
	int indice;

	public Token(String string, Tipo tipo) {
		this.string = string;
		this.tipo = tipo;
	}

	public String getString() {
		return string;
	}

	public int getIndice2() {
		return indice2;
	}

	public int getIndice() {
		return indice;
	}

	public enum Tipo {
		INICIALIZADOR, FINALIZADOR, SEPARADOR, RESERVADO, FLUTUANTE, OPERADOR, IDENTITY, INTEIRO, STRING, CONSTANTE,
		PARAMETRO,
	}

	public Token novo(Tipo tipo) {
		Token token = new Token(string, tipo);
		token.indice2 = indice2;
		token.indice = indice;
		return token;
	}

	public boolean isReservado() {
		return tipo == Tipo.RESERVADO;
	}

	public boolean isString() {
		return tipo == Tipo.STRING;
	}

	@Override
	public String toString() {
		return "Token [string=" + string + ", tipo=" + tipo + "]";
	}
}