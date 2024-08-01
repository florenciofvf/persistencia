package br.com.persist.plugins.instrucao.compilador;

public class Token {
	final String string;
	final int coluna;
	final int linha;
	final Tipo tipo;
	int indice;
	int indice2;

	public Token(String string, int linha, int coluna, Tipo tipo) {
		this.string = string;
		this.coluna = coluna;
		this.linha = linha;
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
		INICIALIZADOR, FINALIZADOR, SEPARADOR, RESERVADO, FLUTUANTE, OPERADOR, IDENTITY, INTEIRO, STRING,
	}

	public boolean isReservado() {
		return tipo == Tipo.RESERVADO;
	}

	public boolean isString() {
		return tipo == Tipo.STRING;
	}

	@Override
	public String toString() {
		return "Token [string=" + string + ", linha=" + linha + ", coluna=" + coluna + ", tipo=" + tipo + "]";
	}
}