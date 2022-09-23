package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.List;

public class ChecagemToken {
	private static final String TOKEN_INVALIDO = " <<< Token invalido >>> ";
	private final String string;
	private int indice;

	public ChecagemToken(String string, boolean completo) {
		this.string = completo ? string : string.trim();
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

	private String getDescartaveis() {
		StringBuilder sb = new StringBuilder();
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c <= ' ') {
				sb.append(c);
				indice++;
			} else {
				break;
			}
		}
		return sb.toString();
	}

	private Token proximoToken(boolean completo) throws ChecagemException {
		if (indice >= string.length()) {
			return null;
		}
		if (completo) {
			String s = getDescartaveis();
			if (!s.isEmpty()) {
				return new Token(s, Token.META, indice);
			}
		} else {
			pularDescartaveis();
		}
		if (indice >= string.length()) {
			return null;
		}
		return proximoTokenImpl();
	}

	private Token proximoTokenImpl() throws ChecagemException {
		char c = string.charAt(indice);
		switch (c) {
		case '\'':
			indice++;
			return tokenString();
		case '$':
			indice++;
			return tokenVariavel();
		case '{':
			indice++;
			return new Token(c, Token.CHAVE_INI, indice);
		case '[':
			indice++;
			return new Token(c, Token.COLCHETE_INI, indice);
		case '(':
			indice++;
			return new Token(c, Token.PARENTESE_INI, indice);
		case ')':
		case '}':
		case ']':
			indice++;
			return new Token(c, Token.PARENTESE_FIM, indice);
		case ':':
		case ',':
			indice++;
			return new Token(c, Token.VIRGULA, indice);
		case '!':
			indice++;
			return new Token(c, Token.AUTO, indice);
		case '+':
		case '-':
		case '*':
		case '/':
		case '%':
		case '^':
		case '=':
			indice++;
			return new Token(c, Token.FUNCAO_INFIXA, indice);
		case '<':
			indice++;
			if (proximoEh('=')) {
				indice++;
				return new Token("<=", Token.FUNCAO_INFIXA, indice);
			} else {
				return new Token(c, Token.FUNCAO_INFIXA, indice);
			}
		case '>':
			indice++;
			if (proximoEh('=')) {
				indice++;
				return new Token(">=", Token.FUNCAO_INFIXA, indice);
			} else {
				return new Token(c, Token.FUNCAO_INFIXA, indice);
			}
		case '&':
			indice++;
			if (proximoEh('&')) {
				indice++;
				return new Token("&&", Token.FUNCAO_INFIXA, indice);
			}
			throw new ChecagemException(getClass(), indice + TOKEN_INVALIDO + c);
		case '|':
			indice++;
			if (proximoEh('|')) {
				indice++;
				return new Token("||", Token.FUNCAO_INFIXA, indice);
			}
			throw new ChecagemException(getClass(), indice + TOKEN_INVALIDO + c);
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
				String s = getString();
				return new Token(s, Token.FUNCAO_PREFIXA, indice);
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
			throw new ChecagemException(getClass(), indice + " <<< String nao encerrada >>> " + sb.toString());
		}
		indice++;
		return new Token(sb.toString(), Token.STRING, indice);
	}

	private Token tokenVariavel() throws ChecagemException {
		StringBuilder sb = new StringBuilder(getString());
		if (sb.length() == 0) {
			throw new ChecagemException(getClass(), indice + " <<< Nome variavel vazio >>> " + indice);
		} else {
			sb.append(getString2());
		}
		return new Token("$" + sb.toString(), Token.VARIAVEL, indice);
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
				return new Token(Double.valueOf(sequencia), Token.DOUBLE, indice);
			} catch (Exception e) {
				throw new ChecagemException(getClass(), indice + " <<< Flutuante invalido >>> " + sequencia);
			}
		}
		try {
			return new Token(Long.valueOf(sequencia), Token.LONG, indice);
		} catch (Exception e) {
			throw new ChecagemException(getClass(), indice + " <<< Inteiro invalido >>> " + sequencia);
		}
	}

	private Token tokenTrueOuPrefixa(char c) {
		String s = getString(c);
		if ("true".equalsIgnoreCase(s)) {
			return new Token(Boolean.TRUE, Token.BOOLEAN, indice);
		}
		return new Token(s, Token.FUNCAO_PREFIXA, indice);
	}

	private Token tokenFalseOuPrefixa(char c) {
		String s = getString(c);
		if ("false".equalsIgnoreCase(s)) {
			return new Token(Boolean.FALSE, Token.BOOLEAN, indice);
		}
		return new Token(s, Token.FUNCAO_PREFIXA, indice);
	}

	private String getString(char d) {
		StringBuilder sb = new StringBuilder("" + d);
		sb.append(getString());
		return sb.toString();
	}

	private boolean validoChar(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private boolean validoChar2(char c) {
		return validoChar(c) || (c >= '0' && c <= '9');
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

	private String getString2() {
		StringBuilder sb = new StringBuilder();
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (validoChar2(c)) {
				sb.append(c);
			} else {
				break;
			}
			indice++;
		}
		return sb.toString();
	}

	public List<Token> getTokens(boolean completo) throws ChecagemException {
		List<Token> lista = new ArrayList<>();
		Token token = proximoToken(completo);
		while (token != null) {
			if (completo || token.getTipo() != Token.META) {
				lista.add(token);
			}
			token = proximoToken(completo);
		}
		if (!completo) {
			normalizar(lista, "-", true);
			normalizar(lista, "+", false);
		}
		return lista;
	}

	private void normalizar(final List<Token> lista, final String op, boolean negar) throws ChecagemException {
		TokenIndice tokenIndice = getTokenIndice(lista, op);
		while (tokenIndice != null) {
			processar(lista, tokenIndice, negar);
			tokenIndice = getTokenIndice(lista, op);
		}
	}

	private TokenIndice getTokenIndice(final List<Token> lista, Object valor) {
		for (int i = 0; i < lista.size(); i++) {
			Token token = lista.get(i);
			if (!token.isProcessado() && valor.equals(token.getValor())) {
				return new TokenIndice(token, i);
			}
		}
		return null;
	}

	private void processar(final List<Token> lista, TokenIndice tokenIndice, boolean negar) throws ChecagemException {
		int antes = tokenIndice.indice - 1;
		int depois = tokenIndice.indice + 1;
		if (isTokenNumero(depois, lista) && !calculavel(antes, lista)) {
			if (negar) {
				negarToken(depois, tokenIndice, lista);
			} else {
				lista.remove(tokenIndice.indice);
			}
		} else if (iniExpressao(depois, lista) && !calculavel(antes, lista)) {
			if (negar) {
				lista.get(depois).setNegarExpressao(true);
			}
			lista.remove(tokenIndice.indice);
		} else {
			tokenIndice.token.setProcessado(true);
		}
	}

	private boolean isTokenNumero(int i, final List<Token> lista) {
		if (i >= 0 && i < lista.size()) {
			Token token = lista.get(i);
			return token.isDouble() || token.isLong();
		}
		return false;
	}

	private boolean iniExpressao(int i, final List<Token> lista) {
		if (i >= 0 && i < lista.size()) {
			Token token = lista.get(i);
			return token.getTipo() == Token.PARENTESE_INI;
		}
		return false;
	}

	private boolean calculavel(int i, List<Token> lista) {
		if (i >= 0 && i < lista.size()) {
			Token token = lista.get(i);
			return token.getTipo() == Token.PARENTESE_FIM || token.getTipo() == Token.VARIAVEL
					|| token.getTipo() == Token.DOUBLE || token.getTipo() == Token.LONG;
		}
		return false;
	}

	private void negarToken(int i, TokenIndice tokenIndice, final List<Token> lista) throws ChecagemException {
		Token token = lista.get(i);
		Token novo = inverter(token, i);
		lista.set(i, novo);
		lista.remove(tokenIndice.indice);
	}

	private Token inverter(Token token, int indice) throws ChecagemException {
		if (token.isDouble()) {
			return new Token(((Double) token.getValor()) * -1, Token.DOUBLE, indice);
		}
		if (token.isLong()) {
			return new Token(((Long) token.getValor()) * -1, Token.LONG, indice);
		}
		throw new ChecagemException(getClass(), indice + " <<< inverter >>> " + token);
	}

	class TokenIndice {
		final Token token;
		final int indice;

		public TokenIndice(Token token, int indice) {
			this.token = token;
			this.indice = indice;
		}
	}
}