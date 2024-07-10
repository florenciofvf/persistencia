package br.com.persist.plugins.instrucao.compilador;

public class Token {
	final String string;
	final int coluna;
	final int linha;
	final Tipo tipo;

	public Token(String string, int linha, int coluna, Tipo tipo) {
		this.string = string;
		this.coluna = coluna;
		this.linha = linha;
		this.tipo = tipo;
	}

	public String getString() {
		return string;
	}

	public enum Tipo {
		INICIALIZADOR, FINALIZADOR, SEPARADOR, RESERVADO, FLUTUANTE, OPERADOR, IDENTITY, INTEIRO, STRING,
	}

	public boolean isReservado() {
		return tipo == Tipo.RESERVADO;
	}

	@Override
	public String toString() {
		return "Token [string=" + string + ", linha=" + linha + ", coluna=" + coluna + ", tipo=" + tipo + "]";
	}
}