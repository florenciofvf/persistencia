package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.List;

public class ChecagemToken {
	private static final String TOKEN_INVALIDO = " <<< Token invalido >>> ";
	private final String string;
	private int indice;

	public ChecagemToken(String string) {
		this.string = string.trim();
		indice = 0;
	}

	private boolean proximoEh(char c) {
		if (indice >= string.length()) {
			return false;
		}
		return string.charAt(indice) == c;
	}

	private void pularDescartaveis() {
		while (indice < string.length()) {
			if (string.charAt(indice) <= ' ') {
				indice++;
			} else {
				break;
			}
		}
	}

	private Token proximoToken() throws ChecagemException {
		if (indice >= string.length()) {
			return null;
		}
		pularDescartaveis();
		if (indice >= string.length()) {
			return null;
		}
		char c = string.charAt(indice);
		switch (c) {
		case '\'':
			indice++;
			return tokenString();
		case '$':
			indice++;
			return tokenVariavel();
		case '(':
			indice++;
			return new Token(c, Token.PARENTESE_INI);
		case ')':
			indice++;
			return new Token(c, Token.PARENTESE_FIM);
		case ',':
			indice++;
			return new Token(c, Token.VIRGULA);
		case '+':
		case '-':
		case '*':
		case '/':
		case '%':
		case '^':
		case '=':
			indice++;
			return new Token(c, Token.FUNCAO_INFIXA);
		case '&':
			indice++;
			if (proximoEh('&')) {
				indice++;
				return new Token("&&", Token.FUNCAO_INFIXA);
			}
			throw new ChecagemException(getClass(), indice + TOKEN_INVALIDO + c);
		case '|':
			indice++;
			if (proximoEh('|')) {
				indice++;
				return new Token("||", Token.FUNCAO_INFIXA);
			}
			throw new ChecagemException(getClass(), indice + TOKEN_INVALIDO + c);
		case '<':
			indice++;
			if (proximoEh('=')) {
				indice++;
				return new Token("<=", Token.FUNCAO_INFIXA);
			} else {
				return new Token(c, Token.FUNCAO_INFIXA);
			}
		case '>':
			indice++;
			if (proximoEh('=')) {
				indice++;
				return new Token(">=", Token.FUNCAO_INFIXA);
			} else {
				return new Token(c, Token.FUNCAO_INFIXA);
			}
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			indice++;
			return tokenNumero(c);
		case 't':
		case 'T':
			indice++;
			return tokenTrueOuPrefixa(c);
		case 'f':
		case 'F':
			indice++;
			return tokenFalseOuPrefixa(c);
		default:
			if (validoChar(c)) {
				String s = getString(c);
				return new Token(s, Token.FUNCAO_PREFIXA);
			}
			throw new ChecagemException(getClass(), indice + TOKEN_INVALIDO + c);
		}
	}

	private Token tokenString() throws ChecagemException {
		StringBuilder sb = new StringBuilder();
		boolean escapeAtivado = false;
		boolean encerrado = false;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c == '\'') {
				if (escapeAtivado) {
					sb.append(c);
					escapeAtivado = false;
				} else {
					encerrado = true;
					break;
				}
			} else if (c == '\\') {
				escapeAtivado = true;
			} else {
				sb.append(c);
				escapeAtivado = false;
			}
			indice++;
		}
		if (!encerrado) {
			throw new ChecagemException(getClass(), "String nao encerrada >>> " + sb.toString());
		}
		indice++;
		return new Token(sb.toString(), Token.STRING);
	}

	private Token tokenVariavel() throws ChecagemException {
		String s = getString();
		if (s.isEmpty()) {
			throw new ChecagemException(getClass(), "Nome variavel vazio >>> " + indice);
		}
		return new Token("$" + s, Token.VARIAVEL);
	}

	private Token tokenNumero(char d) throws ChecagemException {
		StringBuilder sb = new StringBuilder("" + d);
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if ((c >= '0' && c <= '9') || c == '.') {
				sb.append(c);
			} else {
				break;
			}
			indice++;
		}
		String sequencia = sb.toString();
		if (sequencia.indexOf('.') != -1) {
			try {
				return new Token(Double.valueOf(sequencia), Token.DOUBLE);
			} catch (Exception e) {
				throw new ChecagemException(getClass(), "Flutuante invalido >>> " + sequencia);
			}
		}
		try {
			return new Token(Long.valueOf(sequencia), Token.LONG);
		} catch (Exception e) {
			throw new ChecagemException(getClass(), "Inteiro invalido >>> " + sequencia);
		}
	}

	private Token tokenTrueOuPrefixa(char c) {
		String s = getString(c);
		if ("true".equalsIgnoreCase(s)) {
			return new Token(Boolean.TRUE, Token.BOOLEAN);
		}
		return new Token(s, Token.FUNCAO_PREFIXA);
	}

	private Token tokenFalseOuPrefixa(char c) {
		String s = getString(c);
		if ("false".equalsIgnoreCase(s)) {
			return new Token(Boolean.FALSE, Token.BOOLEAN);
		}
		return new Token(s, Token.FUNCAO_PREFIXA);
	}

	private String getString(char d) {
		StringBuilder sb = new StringBuilder("" + d);
		sb.append(getString());
		return sb.toString();
	}

	private boolean validoChar(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private String getString() {
		StringBuilder sb = new StringBuilder();
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (validoChar(c)) {
				sb.append(c);
			} else {
				break;
			}
			indice++;
		}
		return sb.toString();
	}

	public List<Token> getTokens() throws ChecagemException {
		List<Token> lista = new ArrayList<>();
		Token token = proximoToken();
		while (token != null) {
			lista.add(token);
			token = proximoToken();
		}
		return lista;
	}
}