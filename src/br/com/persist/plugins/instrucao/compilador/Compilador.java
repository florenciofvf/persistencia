package br.com.persist.plugins.instrucao.compilador;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;
import br.com.persist.plugins.instrucao.processador.Biblioteca;
import br.com.persist.plugins.instrucao.processador.CacheBiblioteca;

public class Compilador {
	private String string;
	private int indice;
	private int coluna;
	Contexto contexto;
	private int linha;

	public Compilador() {
		coluna = 1;
		linha = 1;
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
			throw new InstrucaoException(file.toString(), false);
		}
		string = getString(file);
		BibliotecaContexto biblioteca = new BibliotecaContexto(file.getName());
		contexto = biblioteca;
		processar();
		if (contexto != biblioteca) {
			throwInstrucaoException();
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
				if (c == '\n') {
					coluna = 1;
					linha++;
				}
				coluna++;
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
			contexto.inicializador(this, new Token("" + c, linha, coluna, Tipo.INICIALIZADOR));
			indice++;
			break;
		case ')':
		case '}':
		case ']':
		case ';':
			contexto.finalizador(this, new Token("" + c, linha, coluna, Tipo.FINALIZADOR));
			indice++;
			break;
		case ',':
			contexto.separador(this, new Token(",", linha, coluna, Tipo.SEPARADOR));
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
			contexto.operador(this, new Token("" + c, linha, coluna, Tipo.OPERADOR));
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
				return new Token(c + "=", linha, coluna, Tipo.OPERADOR);
			} else {
				if (c == '!') {
					throwInstrucaoException();
				}
				return new Token(c + "", linha, coluna, Tipo.OPERADOR);
			}
		} else {
			if (c == '!') {
				throwInstrucaoException();
			}
			return new Token(c + "", linha, coluna, Tipo.OPERADOR);
		}
	}

	private Token tokenString() throws InstrucaoException {
		AtomicBoolean encerrado = new AtomicBoolean(false);
		AtomicBoolean escapar = new AtomicBoolean(false);
		StringBuilder builder = new StringBuilder();
		indice++;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c == '\'') {
				processarApost(builder, c, escapar, encerrado);
				if (encerrado.get()) {
					break;
				}
			} else if (c == '\\') {
				processarBarra(builder, c, escapar);
			} else {
				if (escapar.get()) {
					throwInstrucaoException();
				}
				builder.append(c);
				indice++;
			}
		}
		if (!encerrado.get()) {
			throwInstrucaoException();
		}
		return new Token(builder.toString(), linha, coluna, Tipo.STRING);
	}

	private void processarApost(StringBuilder builder, char c, AtomicBoolean escapar, AtomicBoolean encerrado) {
		if (escapar.get()) {
			builder.append(c);
			escapar.set(false);
		} else {
			encerrado.set(true);
		}
		indice++;
	}

	private void processarBarra(StringBuilder builder, char c, AtomicBoolean escapar) {
		if (escapar.get()) {
			builder.append(c);
			escapar.set(false);
		} else {
			escapar.set(true);
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
			return new Token(builder.toString(), linha, coluna, Tipo.INTEIRO);
		} else {
			String str = builder.toString();
			if (total > 1 || str.endsWith(".")) {
				throwInstrucaoException();
			}
			return new Token(builder.toString(), linha, coluna, Tipo.FLUTUANTE);
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
			return new Token(builder.toString(), linha, coluna, Tipo.RESERVADO);
		}
		return new Token(builder.toString(), linha, coluna, Tipo.IDENTITY);
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