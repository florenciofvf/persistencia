package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.Objects;

public class Atom {
	static final int FUNCAO_INFIXA = 1;
	static final int PARENTESE_INI = 2;
	static final int PARENTESE_FIM = 3;
	static final int BIG_INTEGER = 4;
	static final int BIG_DECIMAL = 5;
	static final int STRING_ATOM = 6;
	static final int CHAVE_INI = 7;
	static final int CHAVE_FIM = 8;
	static final int VARIAVEL = 9;
	static final int VIRGULA = 10;
	static final int STRING = 11;

	private boolean negarExpressao;
	private boolean negarVariavel;
	private boolean processado;
	private final String valor;
	private final int tipo;

	public Atom(String valor, int tipo) {
		this.valor = Objects.requireNonNull(valor);
		this.tipo = tipo;
	}

	public Atom(char c, int tipo) {
		this("" + c, tipo);
	}

	public String getValor() {
		return valor;
	}

	public int getTipo() {
		return tipo;
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

	public boolean isProcessado() {
		return processado;
	}

	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

	public boolean isNegarExpressao() {
		return negarExpressao;
	}

	public void setNegarExpressao(boolean negarExpressao) {
		this.negarExpressao = negarExpressao;
	}

	public boolean isNegarVariavel() {
		return negarVariavel;
	}

	public void setNegarVariavel(boolean negarVariavel) {
		this.negarVariavel = negarVariavel;
	}

	@Override
	public String toString() {
		return valor;
	}
}