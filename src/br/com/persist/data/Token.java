package br.com.persist.data;

public class Token {
	static final byte SEP_ATRIBUTO = 1;
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

	public boolean isSepAtributos() {
		return tipo == SEP_ATRIBUTO;
	}

	public boolean isColcheteIni() {
		return tipo == COLCH_INI;
	}

	public boolean isColcheteFim() {
		return tipo == COLCH_FIM;
	}

	public boolean isChaveIni() {
		return tipo == CHAVE_INI;
	}

	public boolean isChaveFim() {
		return tipo == CHAVE_FIM;
	}

	public boolean isVirgula() {
		return tipo == VIRGULA;
	}

	public boolean isBoolean() {
		return tipo == LOGICO;
	}

	public boolean isString() {
		return tipo == TEXTO;
	}

	public boolean isNumero() {
		return tipo == NUMERO;
	}

	public boolean isNull() {
		return tipo == NULO;
	}

	@Override
	public String toString() {
		return indice + " <<< " + valor.toString();
	}

	public static Tipo criarAtomico(Token token) throws DataException {
		if (token.isBoolean()) {
			return new Logico((Boolean) token.getValor());
		}
		if (token.isNumero()) {
			return new Numero(token.getValor());
		}
		if (token.isString()) {
			return new Texto((String) token.getValor());
		}
		if (token.isNull()) {
			return new Nulo();
		}
		throw new DataException("Invalido criar Atomico para >>> " + token.getValor());
	}
}