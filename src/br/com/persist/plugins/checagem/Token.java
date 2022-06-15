package br.com.persist.plugins.checagem;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.atom.TipoBoolean;
import br.com.persist.plugins.checagem.atom.TipoDouble;
import br.com.persist.plugins.checagem.atom.TipoField;
import br.com.persist.plugins.checagem.atom.TipoLong;
import br.com.persist.plugins.checagem.atom.TipoString;

public class Token {
	public static final int PARENTESE_ABRIR = 1;
	public static final int PARENTESE_FECHA = 2;
	public static final int VIRGULA = 3;
	public static final int BOOLEAN = 4;
	public static final int STRING = 5;
	public static final int DOUBLE = 6;
	public static final int LONG = 7;

	private final String valor;
	private final int tipo;

	public Token(String valor, int tipo) {
		this.valor = valor;
		this.tipo = tipo;
	}

	public String getValor() {
		return valor;
	}

	public int getTipo() {
		return tipo;
	}

	public boolean isParenteseAbrir() {
		return tipo == PARENTESE_ABRIR;
	}

	public boolean isParenteseFechar() {
		return tipo == PARENTESE_FECHA;
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

	boolean isConteudoLong() {
		if (Util.estaVazio(valor)) {
			return false;
		}
		String string = valor.trim();
		for (char c : string.toCharArray()) {
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	boolean isConteudoBoolean() {
		if (Util.estaVazio(valor)) {
			return false;
		}
		String string = valor.trim();
		return "true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string);
	}

	boolean isConteudoDouble() {
		if (Util.estaVazio(valor)) {
			return false;
		}
		String string = valor.trim();
		if (string.startsWith(".") || string.endsWith(".")) {
			return false;
		}
		int pos = string.indexOf('.');
		if (pos == -1) {
			return false;
		}
		String sub = string.substring(0, pos);
		for (char c : sub.toCharArray()) {
			if (c < '0' || c > '9') {
				return false;
			}
		}
		sub = string.substring(pos + 1);
		for (char c : sub.toCharArray()) {
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	public static TipoAtomico criarTipoAtomico(Token token) throws ChecagemException {
		if (token.isParenteseAbrir() || token.isParenteseFechar() || token.isVirgula()) {
			throw new ChecagemException("Invalido criar sentenca para >>> " + token.getValor());
		}
		if (token.isBoolean()) {
			TipoBoolean tipo = new TipoBoolean();
			tipo.setValor(new Boolean(token.getValor()));
			return tipo;
		}
		if (token.isDouble()) {
			TipoDouble tipo = new TipoDouble();
			tipo.setValor(new Double(token.getValor()));
			return tipo;
		}
		if (token.isLong()) {
			TipoLong tipo = new TipoLong();
			tipo.setValor(new Long(token.getValor()));
			return tipo;
		}
		if (token.isString()) {
			if (!Util.estaVazio(token.getValor()) && token.getValor().startsWith("$")) {
				TipoField tipo = new TipoField();
				tipo.setValor(token.getValor());
				return tipo;
			}
			TipoString tipo = new TipoString();
			tipo.setValor(token.getValor());
			return tipo;
		}
		throw new ChecagemException("Invalido criar sentenca para >>> " + token.getValor());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(valor);
		switch (tipo) {
		case PARENTESE_ABRIR:
			sb.append("[PARENTESE_ABRIR]");
			break;
		case PARENTESE_FECHA:
			sb.append("[PARENTESE_FECHA]");
			break;
		case VIRGULA:
			sb.append("[VIRGULA]");
			break;
		case BOOLEAN:
			sb.append("[BOOLEAN]");
			break;
		case STRING:
			sb.append("[STRING]");
			break;
		case DOUBLE:
			sb.append("[DOUBLE]");
			break;
		case LONG:
			sb.append("[LONG]");
			break;
		default:
			sb.append("[INVALIDO]");
		}
		return sb.toString();
	}
}