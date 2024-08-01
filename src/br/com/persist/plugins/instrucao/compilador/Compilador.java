package br.com.persist.plugins.instrucao.compilador;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;
import br.com.persist.plugins.instrucao.processador.Biblioteca;
import br.com.persist.plugins.instrucao.processador.CacheBiblioteca;

public class Compilador {
	private final List<Token> tokens;
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

	public BibliotecaContexto compilar(String arquivo) throws IOException, InstrucaoException {
		if (!CacheBiblioteca.COMPILADOS.isDirectory() && !CacheBiblioteca.COMPILADOS.mkdir()) {
			throw new InstrucaoException(CacheBiblioteca.COMPILADOS.toString(), false);
		}
		File file = new File(CacheBiblioteca.ROOT, arquivo);
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
		biblioteca.indexar();
		File destino = new File(CacheBiblioteca.COMPILADOS, arquivo + Biblioteca.EXTENSAO);
		try (PrintWriter pw = new PrintWriter(destino)) {
			biblioteca.salvar(pw);
		}
		return biblioteca;
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

	private void normal() {
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c <= ' ') {
				indice++;
			} else {
				break;
			}
		}
	}

	private void processarImpl() throws InstrucaoException {
		char c = string.charAt(indice);
		switch (c) {
		case '(':
		case '{':
		case '[':
			contexto.inicializador(this, new Token("" + c, Tipo.INICIALIZADOR));
			indice++;
			break;
		case ')':
		case '}':
		case ']':
		case ';':
			contexto.finalizador(this, new Token("" + c, Tipo.FINALIZADOR));
			indice++;
			break;
		case ',':
			contexto.separador(this, new Token(",", Tipo.SEPARADOR));
			indice++;
			break;
		case '+':
		case '-':
		case '*':
		case '/':
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
			Token token = tokenString();
			if (!token.string.startsWith(":coment")) {
				contexto.string(this, token);
				tokens.add(token);
			} else {
				tokens.add(token.novo(Tipo.COMENTARIO));
			}
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
			contexto.numero(this, tokenNumero());
			break;
		default:
			Token ident = tokenIdentity();
			if (ident.isReservado()) {
				contexto.reservado(this, ident);
				tokens.add(ident);
			} else {
				contexto.identity(this, ident);
			}
		}
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
		Token token = new Token(builder.toString(), Tipo.STRING);
		token.indice2 = indice - 1;
		token.indice = indiceBkp;
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
		} else {
			builder.append(c);
		}
		indice++;
	}

	private Token tokenNumero() throws InstrucaoException {
		StringBuilder builder = new StringBuilder();
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
			return new Token(builder.toString(), Tipo.INTEIRO);
		} else {
			String str = builder.toString();
			if (total > 1 || str.endsWith(".")) {
				throwInstrucaoException();
			}
			return new Token(builder.toString(), Tipo.FLUTUANTE);
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
			Token token = new Token(builder.toString(), Tipo.RESERVADO);
			token.indice = indiceBkp;
			return token;
		}
		Token token = new Token(builder.toString(), Tipo.IDENTITY);
		token.indice = indiceBkp;
		return token;
	}

	private boolean valido1(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || c == '$';
	}

	private boolean valido2(char c) {
		return (c >= '0' && c <= '9') || c == '.';
	}

	private boolean reservado(String s) {
		return "const".equals(s) || "function".equals(s) || "function_native".equals(s) || "if".equals(s)
				|| "elseif".equals(s) || "else".equals(s) || "return".equals(s);
	}
}