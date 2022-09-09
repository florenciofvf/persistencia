package br.com.persist.data;

import java.util.ArrayList;
import java.util.List;

public class DataToken {
	private static final String TOKEN_INVALIDO = " <<< Token invalido >>> ";
	private final String string;
	private int indice;

	public DataToken(String string) {
		this.string = string.trim();
		indice = 0;
	}

	private Token proximoToken() throws DataException {
		if (indice >= string.length()) {
			return null;
		}
		pularDescartaveis();
		if (indice >= string.length()) {
			return null;
		}
		return proximoTokenImpl();
	}

	private Token proximoTokenImpl() throws DataException {
		char c = string.charAt(indice);
		switch (c) {
		case '"':
			indice++;
			return tokenString();
		case ':':
			indice++;
			return new Token(c, Token.SEP_ATRIBUTO, indice);
		case '{':
			indice++;
			return new Token(c, Token.CHAVE_INI, indice);
		case '}':
			indice++;
			return new Token(c, Token.CHAVE_FIM, indice);
		case '[':
			indice++;
			return new Token(c, Token.COLCH_INI, indice);
		case ']':
			indice++;
			return new Token(c, Token.COLCH_FIM, indice);
		case ',':
			indice++;
			return new Token(c, Token.VIRGULA, indice);
		case 't':
			indice++;
			return checkTokenTrue(c);
		case 'f':
			indice++;
			return checkTokenFalse(c);
		case 'n':
			indice++;
			return checkTokenNull(c);
		case '-':
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
		default:
			throw new DataException(indice + TOKEN_INVALIDO + c);
		}
	}

	private Token tokenString() throws DataException {
		StringBuilder sb = new StringBuilder();
		boolean escapeAtivado = false;
		boolean encerrado = false;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c == '\"') {
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
			throw new DataException(indice + " <<< String nao encerrada >>> " + sb.toString());
		}
		indice++;
		return new Token(sb.toString(), Token.TEXTO, indice);
	}

	private Token checkTokenTrue(char c) throws DataException {
		StringBuilder sb = new StringBuilder("" + c);
		sb.append(getStringMinus());
		if ("true".equals(sb.toString())) {
			return new Token(Boolean.TRUE, Token.LOGICO, indice);
		}
		throw new DataException(indice + " <<< Boolean invalido >>> " + sb.toString());
	}

	private Token checkTokenFalse(char c) throws DataException {
		StringBuilder sb = new StringBuilder("" + c);
		sb.append(getStringMinus());
		if ("false".equals(sb.toString())) {
			return new Token(Boolean.FALSE, Token.LOGICO, indice);
		}
		throw new DataException(indice + " <<< Boolean invalido >>> " + sb.toString());
	}

	private Token checkTokenNull(char c) throws DataException {
		StringBuilder sb = new StringBuilder("" + c);
		sb.append(getStringMinus());
		if ("null".equals(sb.toString())) {
			return new Token("null", Token.NULO, indice);
		}
		throw new DataException(indice + " <<< Null invalido >>> " + sb.toString());
	}

	boolean numero(char c) {
		return (c >= '0' && c <= '9') || c == '.' || c == 'E' || c == 'e' || c == '-' || c == '+';
	}

	private Token tokenNumero(char d) throws DataException {
		StringBuilder sb = new StringBuilder("" + d);
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (numero(c)) {
				sb.append(c);
			} else {
				break;
			}
			indice++;
		}
		String s = sb.toString();
		if (s.isEmpty()) {
			throw new DataException(indice + " <<< Numero invalido >>> " + indice);
		}
		return new Token(s, Token.NUMERO, indice);
	}

	public List<Token> getTokens() throws DataException {
		List<Token> lista = new ArrayList<>();
		Token token = proximoToken();
		while (token != null) {
			lista.add(token);
			token = proximoToken();
		}
		return lista;
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

	private boolean minus(char c) {
		return (c >= 'a' && c <= 'z');
	}

	private String getStringMinus() {
		StringBuilder sb = new StringBuilder();
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (minus(c)) {
				sb.append(c);
			} else {
				break;
			}
			indice++;
		}
		return sb.toString();
	}
}