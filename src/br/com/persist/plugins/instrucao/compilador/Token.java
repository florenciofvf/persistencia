package br.com.persist.plugins.instrucao.compilador;

public class Token {
	final String string;
	final int coluna;
	final int linha;
	final Tipo tipo;

	public Token(String string, int coluna, int linha, Tipo tipo) {
		this.string = string;
		this.coluna = coluna;
		this.linha = linha;
		this.tipo = tipo;
	}

	enum Tipo {
		STRING
	}
}