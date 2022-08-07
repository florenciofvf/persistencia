package br.com.persist.plugins.checagem;

import br.com.persist.plugins.checagem.atomico.TipoVariavel;
import br.com.persist.plugins.checagem.atomico.TipoBoolean;
import br.com.persist.plugins.checagem.atomico.TipoDouble;
import br.com.persist.plugins.checagem.atomico.TipoLong;
import br.com.persist.plugins.checagem.atomico.TipoString;

public class Token {
	static final int FUNCAO_PREFIXA = 1;
	static final int FUNCAO_INFIXA = 2;
	static final int PARENTESE_INI = 3;
	static final int PARENTESE_FIM = 4;
	static final int COLCHETE_INI = 5;
	static final int CHAVE_INI = 6;
	static final int VARIAVEL = 7;
	static final int VIRGULA = 8;
	static final int BOOLEAN = 9;
	static final int STRING = 10;
	static final int DOUBLE = 11;
	static final int LONG = 12;
	static final int AUTO = 13;

	private boolean negarExpressao;
	private boolean processado;
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

	public boolean isFuncaoPrefixa() {
		return tipo == FUNCAO_PREFIXA;
	}

	public boolean isFuncaoInfixa() {
		return tipo == FUNCAO_INFIXA;
	}

	public boolean isParenteseIni() {
		return tipo == PARENTESE_INI;
	}

	public boolean isColcheteIni() {
		return tipo == COLCHETE_INI;
	}

	public boolean isChaveIni() {
		return tipo == CHAVE_INI;
	}

	public boolean isParenteseFim() {
		return tipo == PARENTESE_FIM;
	}

	public boolean isVariavel() {
		return tipo == VARIAVEL;
	}

	public boolean isVirgula() {
		return tipo == VIRGULA;
	}

	public boolean isBoolean() {
		return tipo == BOOLEAN;
	}

	public boolean isString() {
		return tipo == STRING;
	}

	public boolean isDouble() {
		return tipo == DOUBLE;
	}

	public boolean isLong() {
		return tipo == LONG;
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
		return indice + " <<< " + valor.toString();
	}

	public static TipoAtomico criarTipoAtomico(Token token) throws ChecagemException {
		if (token.isBoolean()) {
			return new TipoBoolean((Boolean) token.getValor());
		}
		if (token.isVariavel()) {
			return new TipoVariavel((String) token.getValor());
		}
		if (token.isDouble()) {
			return new TipoDouble((Double) token.getValor());
		}
		if (token.isString()) {
			return new TipoString((String) token.getValor());
		}
		if (token.isLong()) {
			return new TipoLong((Long) token.getValor());
		}
		throw new ChecagemException(Token.class, "Invalido criar TipoAtomico para >>> " + token.getValor());
	}
}