package br.com.persist.plugins.instrucao.compilador;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;
import br.com.persist.plugins.instrucao.processador.Biblioteca;
import br.com.persist.plugins.instrucao.processador.CacheBiblioteca;

public class Compilador {
	final List<Token> tokens;
	private String string;
	private int indice;
	Contexto contexto;

	public Compilador() {
		tokens = new ArrayList<>();
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public Contexto getContexto() {
		return contexto;
	}

	public void setContexto(Contexto contexto) {
		this.contexto = contexto;
	}

	public void invalidar(Token token, String erro) throws InstrucaoException {
		throw new InstrucaoException(string.substring(0, indice) + "<<<" + token.string + ">>> " + erro, false);
	}

	public void invalidar(Token token) throws InstrucaoException {
		throw new InstrucaoException(string.substring(0, indice) + "<<<" + token.string + ">>>", false);
	}

	private void throwInstrucaoException(char c) throws InstrucaoException {
		throw new InstrucaoException(string.substring(0, indice) + "<<<" + c + ">>>", false);
	}

	private void throwInstrucaoException() throws InstrucaoException {
		throw new InstrucaoException(string.substring(0, indice), false);
	}

	public BibliotecaContexto compilar(File file) throws IOException, InstrucaoException {
		if (!CacheBiblioteca.COMPILADOS.isDirectory() && !CacheBiblioteca.COMPILADOS.mkdir()) {
			throw new InstrucaoException(CacheBiblioteca.COMPILADOS.toString(), false);
		}
		if (!file.isFile()) {
			throw new InstrucaoException("Inexistente >>> " + file.toString(), false);
		}
		string = getString(file);
		BibliotecaContexto biblioteca = new BibliotecaContexto(file.getName());
		contexto = biblioteca;
		processar();
		if (contexto != biblioteca) {
			throwInstrucaoException();
		}
		if (biblioteca.isEmpty()) {
			return null;
		}
		biblioteca.estruturar();
		biblioteca.indexar();
		biblioteca.desviar();
		biblioteca.validar();
		File destino = getCompilado(file);
		try (PrintWriter pw = new PrintWriter(destino)) {
			biblioteca.salvar(this, pw);
		}
		return biblioteca;
	}

	public static File getCompilado(File arquivo) {
		return new File(CacheBiblioteca.COMPILADOS, arquivo.getName() + Biblioteca.EXTENSAO);
	}

	private String getString(File file) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] bloco = new byte[512];
			int i = fis.read(bloco);
			while (i > 0) {
				baos.write(bloco, 0, i);
				i = fis.read(bloco);
			}
		}
		return new String(baos.toByteArray());
	}

	private void processar() throws InstrucaoException {
		while (indice < string.length()) {
			normal();
			if (indice >= string.length()) {
				return;
			}
			processarImpl();
		}
	}

	private void avancar() {
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c <= ' ') {
				indice++;
			} else {
				break;
			}
		}
	}

	private void normal() {
		StringBuilder sb = new StringBuilder();
		int indiceBkp = indice;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c <= ' ') {
				sb.append(c);
				indice++;
			} else {
				break;
			}
		}
		if (sb.length() > 0) {
			Token token = new Token(sb.toString(), Tipo.TAG, indiceBkp);
			tokens.add(token);
		}
	}

	private void processarImpl() throws InstrucaoException {
		char c = string.charAt(indice);
		Token token = null;
		switch (c) {
		case '(':
		case '{':
			token = new Token("" + c, Tipo.INICIALIZADOR, indice);
			contexto.inicializador(this, token);
			if (c == '{') {
				tokens.add(token);
			}
			indice++;
			break;
		case ')':
		case '}':
		case ';':
			token = new Token("" + c, Tipo.FINALIZADOR, indice);
			contexto.finalizador(this, token);
			if (c == '}') {
				tokens.add(token);
			}
			indice++;
			break;
		case ',':
			contexto.separador(this, new Token(",", Tipo.SEPARADOR));
			indice++;
			break;
		case '/':
			token = operadorOuComentario();
			processarOperadorOuComentario(token);
			break;
		case '+':
		case '-':
		case '*':
		case '%':
		case '^':
		case '&':
		case '|':
		case '=':
		case ':':
			contexto.operador(this, new Token("" + c, Tipo.OPERADOR));
			indice++;
			break;
		case '!':
		case '<':
		case '>':
			contexto.operador(this, tokenOperador());
			break;
		case '\'':
			token = tokenString();
			processarStringOuComentario(token);
			break;
		case '[':
			token = tokenListaOuMapa();
			processarListaOuMapa(token);
			break;
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
			token = tokenNumero();
			contexto.numero(this, token);
			tokens.add(token);
			break;
		default:
			Token ident = tokenIdentity();
			processarReservadoOuIdentity(ident);
		}
	}

	private void processarOperadorOuComentario(Token token) throws InstrucaoException {
		if (!token.string.startsWith("/*")) {
			contexto.operador(this, token);
		} else {
			tokens.add(token.novo(Tipo.COMENTARIO));
		}
	}

	private void processarStringOuComentario(Token token) throws InstrucaoException {
		if (!token.string.startsWith(":coment")) {
			int pos = token.string.indexOf("${");
			if (pos == -1) {
				contexto.string(this, token);
			} else {
				processarString(token);
			}
			tokens.add(token);
		} else {
			tokens.add(token.novo(Tipo.COMENTARIO));
		}
	}

	private void processarString(Token token) throws InstrucaoException {
		List<Atom> lista = gerarAtoms(token);
	}

	private List<Atom> gerarAtoms(Token token) throws InstrucaoException {
		GeraAtom gerador = new GeraAtom(token);
		List<Atom> resp = new ArrayList<>();
		Atom atom = gerador.proximo();
		while (atom != null) {
			resp.add(atom);
			atom = gerador.proximo();
		}
		return resp;
	}

	class GeraAtom {
		final String string;
		final Token token;
		int indice;

		GeraAtom(Token token) {
			this.string = token.string;
			this.token = token;
			indice = 0;
		}

		Atom proximo() throws InstrucaoException {
			return null;
		}
	}

	class Atom {
		final String str;
		final boolean id;

		Atom(String str, boolean id) {
			super();
			this.str = str;
			this.id = id;
		}
	}

	private void processarReservadoOuIdentity(Token ident) throws InstrucaoException {
		if (ident.isReservado()) {
			contexto.reservado(this, ident);
			tokens.add(ident);
		} else {
			contexto.identity(this, ident);
		}
	}

	private void processarListaOuMapa(Token token) throws InstrucaoException {
		if (token.isLista()) {
			contexto.lista(this, token);
		} else {
			contexto.mapa(this, token);
		}
		tokens.add(token);
	}

	private Token operadorOuComentario() throws InstrucaoException {
		char c = string.charAt(indice);
		int indiceBkp = indice;
		indice++;
		if (indice < string.length()) {
			char d = string.charAt(indice);
			if (d == '*') {
				indice++;
				return tokenComentario(indiceBkp);
			} else {
				return new Token(c + "", Tipo.OPERADOR);
			}
		} else {
			return new Token(c + "", Tipo.OPERADOR);
		}
	}

	private Token tokenComentario(int indiceBkp) throws InstrucaoException {
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
			throwInstrucaoException();
		}
		Token token = new Token(str, Tipo.STRING, indiceBkp);
		token.setIndice2(indice);
		return token;
	}

	private Token tokenListaOuMapa() throws InstrucaoException {
		StringBuilder builder = new StringBuilder();
		char c = string.charAt(indice);
		int indiceBkp = indice;
		builder.append(c);
		char d = proximoChar(true);
		indice++;
		Tipo tipo = null;
		if (d == ']') {
			builder.append(d);
			tipo = Tipo.LISTA;
		} else {
			String antes = getElementoLista(d);
			builder.append(antes);
			char e = proximoChar(false);
			if (e != ':' && e != '.') {
				throwInstrucaoException();
			}
			tipo = e == ':' ? Tipo.LISTA : Tipo.MAPA;
			builder.append(e);
			char f = proximoChar(true);
			indice++;
			String depois = getElementoLista(f);
			builder.append(depois);
			char g = proximoChar(false);
			if (g != ']') {
				throwInstrucaoException();
			}
			builder.append(g);
			indice++;
		}
		String str = builder.toString();
		Token token = new Token(str, tipo, indiceBkp);
		token.setIndice2(indice);
		return token;
	}

	private char proximoChar(boolean inc) throws InstrucaoException {
		if (inc) {
			indice++;
		}
		avancar();
		checarIndice();
		return string.charAt(indice);
	}

	private void checarIndice() throws InstrucaoException {
		if (indice >= string.length()) {
			throwInstrucaoException();
		}
	}

	private void checarChar(char c) throws InstrucaoException {
		if (!valido1(c)) {
			throwInstrucaoException();
		}
	}

	private String getElementoLista(char c) throws InstrucaoException {
		checarChar(c);
		StringBuilder sb = new StringBuilder();
		sb.append(c);
		while (indice < string.length()) {
			c = string.charAt(indice);
			if (valido1(c)) {
				sb.append(c);
			} else {
				break;
			}
			indice++;
		}
		return sb.toString();
	}

	private Token tokenOperador() throws InstrucaoException {
		char c = string.charAt(indice);
		indice++;
		if (indice < string.length()) {
			char d = string.charAt(indice);
			if (d == '=') {
				indice++;
				return new Token(c + "=", Tipo.OPERADOR);
			} else {
				if (c == '!') {
					throwInstrucaoException();
				}
				return new Token(c + "", Tipo.OPERADOR);
			}
		} else {
			if (c == '!') {
				throwInstrucaoException();
			}
			return new Token(c + "", Tipo.OPERADOR);
		}
	}

	private Token tokenString() throws InstrucaoException {
		AtomicBoolean encerrado = new AtomicBoolean(false);
		AtomicBoolean escapar = new AtomicBoolean(false);
		StringBuilder builder = new StringBuilder();
		int indiceBkp = indice;
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
			throwInstrucaoException();
		}
		Token token = new Token(builder.toString(), Tipo.STRING, indiceBkp);
		token.setIndice2(indice);
		return token;
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

	private void append(StringBuilder builder, char c, AtomicBoolean escapar) throws InstrucaoException {
		if (escapar.get()) {
			throwInstrucaoException();
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

	private Token tokenNumero() throws InstrucaoException {
		StringBuilder builder = new StringBuilder();
		int indiceBkp = indice;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (valido2(c)) {
				builder.append(c);
			} else {
				break;
			}
			indice++;
		}
		int total = getTotal('.', builder);
		if (total == 0) {
			return new Token(builder.toString(), Tipo.INTEIRO, indiceBkp);
		} else {
			String str = builder.toString();
			if (total > 1 || str.endsWith(".")) {
				throwInstrucaoException();
			}
			return new Token(builder.toString(), Tipo.FLUTUANTE, indiceBkp);
		}
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

	private Token tokenIdentity() throws InstrucaoException {
		StringBuilder builder = new StringBuilder();
		char c = string.charAt(indice);
		int indiceBkp = indice;
		if (valido1(c)) {
			builder.append(c);
			indice++;
		} else {
			throwInstrucaoException(c);
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
		if (total > 1 || str.endsWith(".")) {
			throwInstrucaoException();
		}
		if (reservado(str)) {
			return new Token(builder.toString(), Tipo.RESERVADO, indiceBkp);
		}
		return new Token(builder.toString(), Tipo.IDENTITY, indiceBkp);
	}

	private static boolean valido1(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || c == '$';
	}

	private static boolean valido2(char c) {
		return (c >= '0' && c <= '9') || c == '.';
	}

	public static boolean valido3(char c) {
		return valido1(c) || valido2(c);
	}

	private boolean reservado(String s) {
		return InstrucaoConstantes.CONST.equals(s) || InstrucaoConstantes.DEFUN.equals(s)
				|| InstrucaoConstantes.DEFUN_NATIVE.equals(s) || InstrucaoConstantes.IF.equals(s)
				|| InstrucaoConstantes.WHILE.equals(s) || InstrucaoConstantes.ELSEIF.equals(s)
				|| InstrucaoConstantes.ELSE.equals(s) || InstrucaoConstantes.RETURN.equals(s);
	}

	void checarTailCall(Token token) throws InstrucaoException {
		if (InstrucaoConstantes.TAILCALL.equals(token.getString())) {
			invalidar(token);
		}
	}
}