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
		String s = getString();
		if (s.isEmpty()) {
			throw new ChecagemException(getClass(), indice + " <<< Nome variavel vazio >>> " + indice);
		}
		return new Token("$" + s, Token.VARIAVEL, indice);
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
		return normalizar(lista);
	}

	private List<Token> normalizar(List<Token> lista) throws ChecagemException {
		final String op = "-";
		TokenIndice obj = getTokenIndice(lista, op);
		while (obj != null) {
			processar(obj, lista);
			obj = getTokenIndice(lista, op);
		}
		return lista;
	}

	private void processar(TokenIndice obj, List<Token> lista) throws ChecagemException {
		int antes = obj.indice - 1;
		int apos = obj.indice + 1;
		if ((obj.indice == 0 && isNumero(apos, lista))
				|| (isTipo(antes, lista, Token.PARENTESE_INI) && isNumero(apos, lista))) {
			negativar(obj, lista, apos);
		}
	}

	private void negativar(TokenIndice obj, List<Token> lista, int apos) throws ChecagemException {
		Token proximo = lista.get(apos);
		Token novo = negar(proximo, apos);
		lista.set(apos, novo);
		lista.remove(obj.indice);
	}

	private Token negar(Token token, int indice) throws ChecagemException {
		if (token.isDouble()) {
			return new Token(((Double) token.getValor()) * -1, Token.DOUBLE, indice);
		}
		if (token.isLong()) {
			return new Token(((Long) token.getValor()) * -1, Token.LONG, indice);
		}
		throw new ChecagemException(getClass(), indice + " <<< negar >>> " + token);
	}

	private boolean isNumero(int i, List<Token> lista) {
		if (i < lista.size()) {
			Token token = lista.get(i);
			if (token.isDouble() || token.isLong()) {
				return true;
			}
		}
		return false;
	}

	private boolean isTipo(int i, List<Token> lista, int tipo) {
		if (i < lista.size()) {
			Token token = lista.get(i);
			if (token.getTipo() == tipo) {
				return true;
			}
		}
		return false;
	}

	private TokenIndice getTokenIndice(List<Token> lista, Object valor) {
		for (int i = 0; i < lista.size(); i++) {
			Token token = lista.get(i);
			if (!token.isProcessado() && valor.equals(token.getValor())) {
				return new TokenIndice(token, i);
			}
		}
		return null;
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