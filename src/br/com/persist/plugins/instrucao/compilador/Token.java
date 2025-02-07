package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;

public class Token {
	final String string;
	private int indice2;
	private int indice;
	boolean ignorarCor;
	final Tipo tipo;

	public Token(String string, Tipo tipo, int indice) {
		this.string = string;
		this.indice = indice;
		this.tipo = tipo;
	}

	public Token(String string, Tipo tipo, boolean ignorarCor) {
		this(string, tipo, 0);
		this.ignorarCor = ignorarCor;
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
		INICIALIZADOR, FINALIZADOR, SEPARADOR, RESERVADO, FLUTUANTE, OPERADOR, IDENTITY, INTEIRO, STRING, LISTA, MAPA,
		CONSTANTE, PARAMETRO, COMENTARIO, FUNCAO, TAG
	}

	public Token novo(Tipo tipo) {
		Token token = new Token(string, tipo, indice);
		token.ignorarCor = ignorarCor;
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

	public boolean isIdentity() {
		return tipo == Tipo.IDENTITY;
	}

	public boolean isFuncao() {
		return tipo == Tipo.FUNCAO;
	}

	public boolean isString() {
		return tipo == Tipo.STRING;
	}

	public boolean isLista() {
		return tipo == Tipo.LISTA;
	}

	public boolean isMapa() {
		return tipo == Tipo.MAPA;
	}

	public boolean isLamb() {
		return InstrucaoConstantes.LAMB.equals(string);
	}

	public boolean isEspecial() {
		return tipo == Tipo.INICIALIZADOR || tipo == Tipo.FINALIZADOR;
	}

	public boolean isNumero() {
		return tipo == Tipo.INTEIRO || tipo == Tipo.FLUTUANTE;
	}

	public boolean isTag() {
		return tipo == Tipo.TAG;
	}

	public boolean isIgnorarCor() {
		return ignorarCor;
	}

	public void setIgnorarCor(boolean ignorarCor) {
		this.ignorarCor = ignorarCor;
	}

	@Override
	public String toString() {
		return "Token [string=" + string + ", tipo=" + tipo + ", indice=" + indice + "]";
	}
}