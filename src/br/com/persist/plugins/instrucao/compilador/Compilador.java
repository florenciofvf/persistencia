package br.com.persist.plugins.instrucao.compilador;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;

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

	private void throwInstrucaoException() throws InstrucaoException {
		throw new InstrucaoException(string.substring(0, indice), false);
	}

	public void compilar(String arquivo) throws IOException, InstrucaoException {
		if (!CacheBiblioteca.COMPILADOS.isDirectory() && !CacheBiblioteca.COMPILADOS.mkdir()) {
			return;
		}
		File file = new File(CacheBiblioteca.ROOT, arquivo);
		if (!file.isFile()) {
			return;
		}
		string = getString(file);
		processar();
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
		case '\'':
			indice++;
			tokenString();
			break;
		}
	}

	private void tokenString() throws InstrucaoException {
		AtomicBoolean encerrado = new AtomicBoolean(false);
		AtomicBoolean escapar = new AtomicBoolean(false);
		StringBuilder builder = new StringBuilder();
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
			}
			indice++;
		}
		if (!encerrado.get()) {
			throwInstrucaoException();
		}
		indice++;
		Token token = new Token(builder.toString(), coluna, linha, Tipo.STRING);
		if (contexto != null) {
			contexto.string(this, token);
		}
	}

	private void processarApost(StringBuilder builder, char c, AtomicBoolean escapar, AtomicBoolean encerrado) {
		if (escapar.get()) {
			builder.append(c);
			escapar.set(false);
		} else {
			encerrado.set(true);
		}
	}

	private void processarBarra(StringBuilder builder, char c, AtomicBoolean escapar) {
		if (escapar.get()) {
			builder.append(c);
			escapar.set(false);
		} else {
			escapar.set(true);
		}
	}
}