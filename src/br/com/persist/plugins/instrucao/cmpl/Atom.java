package br.com.persist.plugins.instrucao.cmpl;

import java.util.Objects;

public class Atom {
	static final int FUNCAO_INFIXA = 1;
	static final int PARENTESE_INI = 2;
	static final int PARENTESE_FIM = 3;
	static final int BIG_INTEGER = 4;
	static final int BIG_DECIMAL = 5;
	static final int STRING_ATOM = 6;
	static final int COMENTARIO = 7;
	static final int CHAVE_INI = 8;
	static final int CHAVE_FIM = 9;
	static final int VARIAVEL = 10;
	static final int VIRGULA = 11;
	static final int STRING = 12;
	static final int PARAM = 13;

	private boolean processado;
	private final String valor;
	private int lengthOffset;
	private final int tipo;
	private boolean negar;
	private int indice;

	public Atom(String valor, int tipo, int indice) {
		this.valor = Objects.requireNonNull(valor);
		this.indice = indice;
		this.tipo = tipo;
	}

	public Atom(char c, int tipo, int indice) {
		this("" + c, tipo, indice);
	}

	public String getValor() {
		return valor;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public int getIndice() {
		return indice;
	}

	public int getTipo() {
		return tipo;
	}

	public int getLengthOffset() {
		return lengthOffset;
	}

	public void setLengthOffset(int lengthOffset) {
		this.lengthOffset = lengthOffset;
	}

	public boolean isFuncaoInfixa() {
		return tipo == FUNCAO_INFIXA;
	}

	public boolean isParenteseIni() {
		return tipo == PARENTESE_INI;
	}

	public boolean isParenteseFim() {
		return tipo == PARENTESE_FIM;
	}

	public boolean isBigInteger() {
		return tipo == BIG_INTEGER;
	}

	public boolean isBigDecimal() {
		return tipo == BIG_DECIMAL;
	}

	public boolean isComentario() {
		return tipo == COMENTARIO;
	}

	public boolean isStringAtom() {
		return tipo == STRING_ATOM;
	}

	public boolean isVariavel() {
		return tipo == VARIAVEL;
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

	public boolean isString() {
		return tipo == STRING;
	}

	public boolean isParam() {
		return tipo == PARAM;
	}

	public boolean isProcessado() {
		return processado;
	}

	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

	public boolean isNegar() {
		return negar;
	}

	public void setNegar(boolean negar) {
		this.negar = negar;
	}

	@Override
	public String toString() {
		return valor;
	}
}