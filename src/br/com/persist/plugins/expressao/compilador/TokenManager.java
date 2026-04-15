package br.com.persist.plugins.expressao.compilador;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Token.Tipo;

public class TokenManager {
	protected final String string;
	protected List<Token> tokens;
	private Contexto selecionado;
	private int indice;

	public TokenManager(String string) {
		this.string = Objects.requireNonNull(string);
		tokens = new ArrayList<>();
	}

	public void addToken(Token token) {
		if (token != null) {
			tokens.add(token);
		}
	}

	public void removeToken(Token token) {
		if (token != null) {
			tokens.remove(token);
		}
	}

	public void invalidar(Token token) throws ExpressaoException {
		int indiceFinal = token.indice + token.getString().length();
		String detalhe = string.substring(0, indiceFinal);
		throw new ExpressaoException(detalhe + "<<<", false);
	}

	public void invalidar() throws ExpressaoException {
		String detalhe = string.substring(0, indice + 1);
		throw new ExpressaoException(detalhe + "<<<", false);
	}

	public void selecionarParentDe(Contexto contexto) throws ExpressaoException {
		if (contexto == null) {
			throw new ExpressaoException("erro.tokenManager.contexto_nulo_em", "selecionarParentDe");
		}
		Contexto parent = contexto.parent;
		if (parent == null) {
			throw new ExpressaoException("erro.tokenManager.contexto_nulo_em", "selecionarParentDe (parent nulo)");
		}
		selecionado = parent;
		selecionado.selecionadoVia(this, contexto);
	}

	public void selecionar(Contexto contexto) {
		this.selecionado = contexto;
	}

	public Contexto getSelecionado() {
		return selecionado;
	}

	public void montarHierarquia() throws ExpressaoException {
		tokens.clear();
		indice = 0;
		while (indice < string.length()) {
			saltarCharMenorOuIgualA32();
			if (indice >= string.length()) {
				return;
			}
			Token token = proximoToken();
			addToken(token);
			processar(token);
		}
	}

	public void processar(Token token) throws ExpressaoException {
		if (selecionado == null) {
			throw new ExpressaoException("erro.tokenManager.contexto_nulo");
		}
		if (token == null) {
			throw new ExpressaoException("erro.tokenManager.token_nulo");
		}
		if (token.tipo != Tipo.COMENTARIO) {
			selecionado.processarPre(this, token);
			if (!token.isConsumido()) {
				selecionado.processar(this, token);
			}
		}
	}

	private void saltarCharMenorOuIgualA32() {
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c <= ' ') {
				indice++;
			} else {
				break;
			}
		}
	}

	private Token tokenGrupo1(char c, int indiceBackup) throws ExpressaoException {
		indice++;
		switch (c) {
		case '(':
			return new Token("" + c, Tipo.ABRE_PARENTESE, indiceBackup);
		case ')':
			return new Token("" + c, Tipo.FECHA_PARENTESE, indiceBackup);
		case '[':
			return new Token("" + c, Tipo.ABRE_COLCHETE, indiceBackup);
		case ']':
			return new Token("" + c, Tipo.FECHA_COLCHETE, indiceBackup);
		case '{':
			return new Token("" + c, Tipo.ABRE_CHAVE, indiceBackup);
		case '}':
			return new Token("" + c, Tipo.FECHA_CHAVE, indiceBackup);
		case ';':
			return new Token("" + c, Tipo.PONTO_E_VIRGULA, indiceBackup);
		case ',':
			return new Token("" + c, Tipo.VIRGULA, indiceBackup);
		default:
			throw new ExpressaoException("token.operador.1");
		}
	}

	private Token tokenOperador1(char c, int indiceBackup) throws ExpressaoException {
		switch (c) {
		case '+':
			invalidarSe(indice + 1, '+');
			indice++;
			return new Token("" + c, Tipo.OPERADOR, indiceBackup);
		case ':':
			invalidarSe(indice + 1, ':');
			indice++;
			return new Token("" + c, Tipo.OPERADOR, indiceBackup);
		case '-':
			invalidarSe(indice + 1, '-');
			indice++;
			return new Token("" + c, Tipo.OPERADOR, indiceBackup);
		case '*':
			invalidarSe(indice + 1, '*');
			indice++;
			return new Token("" + c, Tipo.OPERADOR, indiceBackup);
		case '%':
			invalidarSe(indice + 1, '%');
			indice++;
			return new Token("" + c, Tipo.OPERADOR, indiceBackup);
		case '^':
			invalidarSe(indice + 1, '^');
			indice++;
			return new Token("" + c, Tipo.OPERADOR, indiceBackup);
		default:
			throw new ExpressaoException("token.operador.2");
		}
	}

	private Token tokenOperador2(char c, int indiceBackup) throws ExpressaoException {
		switch (c) {
		case '=':
			if (diferenteDe(indice + 1, '=')) {
				indice++;
				return new Token("" + c, Tipo.ATRIBUICAO, indiceBackup);
			}
			indice++;
			invalidarSe(indice + 1, '=');
			indice++;
			return new Token("==", Tipo.OPERADOR, indiceBackup);
		case '!':
			if (diferenteDe(indice + 1, '=')) {
				invalidar();
			}
			indice++;
			invalidarSe(indice + 1, '=');
			indice++;
			return new Token("!=", Tipo.OPERADOR, indiceBackup);
		case '&':
			if (diferenteDe(indice + 1, '&')) {
				invalidar();
			}
			indice++;
			invalidarSe(indice + 1, '&');
			indice++;
			return new Token("&&", Tipo.OPERADOR, indiceBackup);
		case '|':
			if (diferenteDe(indice + 1, '|')) {
				invalidar();
			}
			indice++;
			invalidarSe(indice + 1, '|');
			indice++;
			return new Token("||", Tipo.OPERADOR, indiceBackup);
		case '.':
			if (diferenteDe(indice + 1, '.')) {
				invalidar();
			}
			indice++;
			invalidarSe(indice + 1, '.');
			indice++;
			return new Token("..", Tipo.OPERADOR, indiceBackup);
		case '<':
			if (diferenteDe(indice + 1, '=')) {
				indice++;
				return new Token("" + c, Tipo.OPERADOR, indiceBackup);
			}
			indice++;
			invalidarSe(indice + 1, '=');
			indice++;
			return new Token("<=", Tipo.OPERADOR, indiceBackup);
		case '>':
			if (diferenteDe(indice + 1, '=')) {
				indice++;
				return new Token("" + c, Tipo.OPERADOR, indiceBackup);
			}
			indice++;
			invalidarSe(indice + 1, '=');
			indice++;
			return new Token(">=", Tipo.OPERADOR, indiceBackup);
		default:
			throw new ExpressaoException("token.operador.3");
		}
	}

	private Token tokenEL(int indiceBackup) throws ExpressaoException {
		if (diferenteDe(indiceBackup + 1, '{')) {
			invalidar();
		}
		StringBuilder builder = new StringBuilder();
		indice = indiceBackup + 2;
		boolean finalizado = false;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c == '>') {
				builder.append(",");
			} else if (valido1(c) || valido2(c) || c == ':') {
				builder.append(c);
			} else if (c == '}') {
				finalizado = true;
				indice++;
				break;
			} else if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
				//
			} else {
				invalidar();
			}
			indice++;
		}
		if (!finalizado) {
			invalidar();
		}
		return new Token(builder.toString(), Tipo.EL, indiceBackup);
	}

	private Token proximoToken() throws ExpressaoException {
		char c = string.charAt(indice);
		int indiceBackup = indice;
		switch (c) {
		case '(':
		case ')':
		case '{':
		case '}':
		case '[':
		case ']':
		case ';':
		case ',':
			return tokenGrupo1(c, indiceBackup);
		case '+':
		case '-':
		case '*':
		case '%':
		case '^':
			return tokenOperador1(c, indiceBackup);
		case '=':
		case '!':
		case '&':
		case '|':
		case '<':
		case '>':
			return tokenOperador2(c, indiceBackup);
		case '$':
			return tokenEL(indiceBackup);
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
			return tokenNumero(c, indiceBackup);
		case '\'':
			return tokenString(indiceBackup);
		case '/':
			if (igualA(indice + 1, '/')) {
				indice += 2;
				return tokenComentarioLinha(indiceBackup);
			} else if (igualA(indice + 1, '*')) {
				indice += 2;
				return tokenComentarioMultiplasLinhas(indiceBackup);
			}
			indice++;
			return new Token("" + c, Tipo.OPERADOR, indiceBackup);
		default:
			return tokenChave(indiceBackup);
		}
	}

	private static boolean valido1(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private static boolean valido2(char c) {
		return (c >= '0' && c <= '9') || c == '.';
	}

	private Token tokenChave(int indiceBackup) throws ExpressaoException {
		StringBuilder builder = new StringBuilder();
		char c = string.charAt(indice);
		if (valido1(c)) {
			builder.append(c);
			indice++;
		} else {
			invalidar();
		}
		while (indice < string.length()) {
			c = string.charAt(indice);
			if (valido1(c) || valido2(c)) {
				builder.append(c);
			} else {
				break;
			}
			indice++;
		}
		int total = getTotal('.', builder);
		String str = builder.toString();
		if (str.endsWith(".") || str.indexOf("..") != -1) {
			invalidar();
		}
		if (total == 0) {
			return new Token(str, Tipo.CHAVE, indiceBackup);
		} else if (total == 1) {
			return new Token(str, Tipo.CHAVE2, indiceBackup);
		}
		return new Token(str, Tipo.CHAVEN, indiceBackup);
	}

	private Token tokenComentarioLinha(int indiceBackup) {
		StringBuilder builder = new StringBuilder("//");
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c == '\n') {
				indice++;
				break;
			} else {
				builder.append(c);
			}
			indice++;
		}
		return new Token(builder.toString(), Tipo.COMENTARIO, indiceBackup);
	}

	private Token tokenComentarioMultiplasLinhas(int indiceBackup) throws ExpressaoException {
		StringBuilder builder = new StringBuilder("/*");
		while (indice < string.length()) {
			char c = string.charAt(indice);
			builder.append(c);
			indice++;
			int len = builder.length() - 1;
			int ant = len - 1;
			if (builder.charAt(ant) == '*' && builder.charAt(len) == '/') {
				break;
			}
		}
		String str = builder.toString();
		if (str.startsWith("/*/") || !str.endsWith("*/")) {
			invalidar();
		}
		return new Token(builder.toString(), Tipo.COMENTARIO, indiceBackup);
	}

	private Token tokenString(int indiceBackup) throws ExpressaoException {
		AtomicBoolean encerrado = new AtomicBoolean(false);
		AtomicBoolean escapar = new AtomicBoolean(false);
		StringBuilder builder = new StringBuilder();
		indice++;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			switch (c) {
			case '\'':
				apostrofe(builder, c, escapar, encerrado);
				break;
			case '\\':
				barra(builder, c, escapar);
				break;
			case 'R':
			case 'N':
			case 'T':
				outros(builder, c, escapar);
				break;
			default:
				append(builder, c, escapar);
			}
			if (encerrado.get()) {
				break;
			}
		}
		if (!encerrado.get()) {
			invalidar();
		}
		return new Token(builder.toString(), Tipo.STRING, indiceBackup);
	}

	private void apostrofe(StringBuilder builder, char c, AtomicBoolean escapar, AtomicBoolean encerrado) {
		if (escapar.get()) {
			builder.append(c);
			escapar.set(false);
		} else {
			encerrado.set(true);
		}
		indice++;
	}

	private void barra(StringBuilder builder, char c, AtomicBoolean escapar) {
		if (escapar.get()) {
			builder.append(c);
			escapar.set(false);
		} else {
			escapar.set(true);
		}
		indice++;
	}

	private void outros(StringBuilder builder, char c, AtomicBoolean escapar) {
		if (escapar.get()) {
			builder.append('\\');
			escapar.set(false);
		}
		builder.append(c);
		indice++;
	}

	private void append(StringBuilder builder, char c, AtomicBoolean escapar) throws ExpressaoException {
		if (escapar.get()) {
			invalidar();
		}
		if (c == '\r') {
			builder.append("\\R");
		} else if (c == '\n') {
			builder.append("\\N");
		} else if (c == '\t') {
			builder.append("\\T");
		} else {
			builder.append(c);
		}
		indice++;
	}

	private Token tokenNumero(char primeiro, int indiceBackup) throws ExpressaoException {
		StringBuilder builder = new StringBuilder();
		builder.append(primeiro);
		indice++;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (numeroOuPonto(c)) {
				builder.append(c);
			} else {
				break;
			}
			indice++;
		}
		int total = getTotal('.', builder);
		if (total == 0) {
			return new Token(builder.toString(), Tipo.INTEIRO, indiceBackup);
		} else {
			String str = builder.toString();
			if (total > 1 || str.endsWith(".")) {
				invalidar();
			}
			return new Token(builder.toString(), Tipo.FLUTUANTE, indiceBackup);
		}
	}

	private static boolean numeroOuPonto(char c) {
		return (c >= '0' && c <= '9') || c == '.';
	}

	private int getTotal(char c, StringBuilder sb) {
		int total = 0;
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == c) {
				total++;
			}
		}
		return total;
	}

	public static int getTotal(char c, String string) {
		int total = 0;
		for (char item : string.toCharArray()) {
			if (item == c) {
				total++;
			}
		}
		return total;
	}

	private void invalidarSe(int indice, char c) throws ExpressaoException {
		if (indice < string.length() && string.charAt(indice) == c) {
			invalidar();
		}
	}

	private boolean igualA(int indice, char c) {
		return indice < string.length() && string.charAt(indice) == c;
	}

	private boolean diferenteDe(int indice, char c) throws ExpressaoException {
		if (indice < string.length()) {
			return string.charAt(indice) != c;
		}
		String detalhe = string.substring(0, indice);
		throw new ExpressaoException(detalhe, false);
	}
}