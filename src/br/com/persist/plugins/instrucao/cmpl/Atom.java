package br.com.persist.plugins.instrucao.cmpl;

public class Atom {
	static final int FUNCAO_INFIXA = 1;
	static final int PARENTESE_INI = 2;
	static final int PARENTESE_FIM = 3;
	static final int STRING_ATOM = 4;
	static final int BIG_INTEGER = 5;
	static final int BIG_DECIMAL = 6;
	static final int VIRGULA = 7;
	static final int STRING = 8;
	static final int AUTO = 9;
	static final int META = 10;

	private boolean negarExpressao;
	private boolean processado;
	private final Object valor;
	private final int tipo;

	public Atom(Object valor, int tipo) {
		this.valor = valor;
		this.tipo = tipo;
	}

	public Atom(char c, int tipo) {
		this("" + c, tipo);
	}

	public Object getValor() {
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

	public boolean isVirgula() {
		return tipo == VIRGULA;
	}

	public boolean isString() {
		return tipo == STRING;
	}

	public boolean isAuto() {
		return tipo == AUTO;
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

	@Override
	public String toString() {
		return valor.toString();
	}
}