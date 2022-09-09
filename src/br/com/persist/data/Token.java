package br.com.persist.data;

public class Token {
	static final byte DOIS_PONTO = 1;
	static final byte CHAVE_INI = 2;
	static final byte CHAVE_FIM = 3;
	static final byte COLCH_INI = 4;
	static final byte COLCH_FIM = 5;
	static final int VIRGULA = 6;
	static final byte NUMERO = 7;
	static final byte LOGICO = 8;
	static final byte TEXTO = 9;
	static final byte NULO = 10;

	private final Object valor;
	private final int indice;
	private final int tipo;

	public Token(Object valor, int tipo, int indice) {
		this.indice = indice;
		this.valor = valor;
		this.tipo = tipo;
	}

	public Token(char c, int tipo, int indice) {
		this("" + c, tipo, indice);
	}

	public Object getValor() {
		return valor;
	}

	public int getIndice() {
		return indice;
	}

	public int getTipo() {
		return tipo;
	}

	@Override
	public String toString() {
		return indice + " <<< " + valor.toString();
	}
}