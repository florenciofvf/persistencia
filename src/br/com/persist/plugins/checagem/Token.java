package br.com.persist.plugins.checagem;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.atomico.TipoAtributoContexto;
import br.com.persist.plugins.checagem.atomico.TipoBoolean;
import br.com.persist.plugins.checagem.atomico.TipoDouble;
import br.com.persist.plugins.checagem.atomico.TipoLong;
import br.com.persist.plugins.checagem.atomico.TipoString;

public class Token {
	static final int PARENTESE_INI = 1;
	static final int PARENTESE_FIM = 2;
	static final int FUNCAO_INFIXA = 8;
	static final int VIRGULA = 3;
	static final int BOOLEAN = 4;
	static final int STRING = 5;
	static final int DOUBLE = 6;
	static final int LONG = 7;

	private final Object valor;
	private final int tipo;

	public Token(Object valor, int tipo) {
		this.valor = valor;
		this.tipo = tipo;
	}

	public Token(char c, int tipo) {
		this("" + c, tipo);
	}

	public Object getValor() {
		return valor;
	}

	public int getTipo() {
		return tipo;
	}

	public boolean isParenteseAbrir() {
		return tipo == PARENTESE_INI;
	}

	public boolean isParenteseFechar() {
		return tipo == PARENTESE_FIM;
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

	public static TipoAtomico criarTipoAtomico(Token token) throws ChecagemException {
		if (token.isBoolean()) {
			TipoBoolean tipo = new TipoBoolean();
			tipo.setValor((Boolean) token.getValor());
			return tipo;
		}
		if (token.isDouble()) {
			TipoDouble tipo = new TipoDouble();
			tipo.setValor((Double) token.getValor());
			return tipo;
		}
		if (token.isLong()) {
			TipoLong tipo = new TipoLong();
			tipo.setValor((Long) token.getValor());
			return tipo;
		}
		if (token.isString()) {
			String valor = token.getValor().toString();
			if (!Util.estaVazio(valor) && valor.startsWith("$")) {
				TipoAtributoContexto tipo = new TipoAtributoContexto();
				tipo.setValor(valor);
				return tipo;
			}
			TipoString tipo = new TipoString();
			tipo.setValor(valor);
			return tipo;
		}
		throw new ChecagemException(Token.class, "Invalido criar TipoAtomico para >>> " + token.getValor());
	}

	@Override
	public String toString() {
		return valor.toString();
	}
}