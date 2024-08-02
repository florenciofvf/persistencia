package br.com.persist.plugins.instrucao.compilador;

public class Token {
	final String string;
	private int indice2;
	private int indice;
	final Tipo tipo;

	public Token(String string, Tipo tipo, int indice) {
		this.string = string;
		this.indice = indice;
		this.tipo = tipo;
	}

	public Token(String string, Tipo tipo) {
		this(string, tipo, 0);
	}

	public void setIndice2(int indice2) {
		this.indice2 = indice2;
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
		PARAMETRO, COMENTARIO
	}

	public Token novo(Tipo tipo) {
		Token token = new Token(string, tipo, indice);
		token.indice2 = indice2;
		return token;
	}

	public boolean isConstante() {
		return tipo == Tipo.CONSTANTE;
	}

	public boolean isParametro() {
		return tipo == Tipo.PARAMETRO;
	}

	public boolean isComentario() {
		return tipo == Tipo.COMENTARIO;
	}

	public boolean isReservado() {
		return tipo == Tipo.RESERVADO;
	}

	public boolean isString() {
		return tipo == Tipo.STRING;
	}

	public boolean isEspecial() {
		return tipo == Tipo.INICIALIZADOR || tipo == Tipo.FINALIZADOR;
	}

	public boolean isNumero() {
		return tipo == Tipo.INTEIRO || tipo == Tipo.FLUTUANTE;
	}

	@Override
	public String toString() {
		return "Token [string=" + string + ", tipo=" + tipo + "]";
	}
}