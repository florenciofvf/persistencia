package br.com.persist.assistencia;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubstLinhaArquivo {
	public static void main(String[] args) throws Exception {
		List<Arquivo> arquivos = criarArquivos();
		for (Arquivo arquivo : arquivos) {
			arquivo.processar();
		}
	}

	private static List<Arquivo> criarArquivos() {
		List<Arquivo> resposta = new ArrayList<>();
		resposta.add(new Arquivo("teste.tmp", new Linha(2, "Teste")));
		return resposta;
	}
}

class Arquivo {
	private static final Logger LOG = Logger.getGlobal();
	final String absoluto;
	final Linha linha;

	Arquivo(String absoluto, Linha linha) {
		this.absoluto = absoluto;
		this.linha = linha;
	}

	void processar() throws IOException {
		List<String> arquivo = lerArquivo();
		if (linha.numero < 1 || linha.numero > arquivo.size()) {
			return;
		}
		char c = ultimo();
		PrintWriter pw = criarPrintWriter();
		for (int i = 0, num = 1; i < arquivo.size(); i++, num++) {
			String string = arquivo.get(i);
			linha.processar(string, num, pw, num < arquivo.size());
		}
		if (c == '\r' || c == '\n') {
			pw.print(c);
		}
		pw.close();
		LOG.log(Level.INFO, "PROCESSADO >>> {0}", absoluto);
	}

	private List<String> lerArquivo() throws IOException {
		List<String> resposta = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(absoluto)))) {
			String string = br.readLine();
			while (string != null) {
				resposta.add(string);
				string = br.readLine();
			}
		}
		return resposta;
	}

	private char ultimo() throws IOException {
		try (RandomAccessFile raf = new RandomAccessFile(absoluto, "r")) {
			long length = raf.length();
			raf.seek(length - 1);
			return (char) raf.read();
		}
	}

	private PrintWriter criarPrintWriter() throws FileNotFoundException {
		return new PrintWriter(absoluto);
	}

	Linha getLinhaArquivo(int num) throws IOException {
		List<String> arquivo = lerArquivo();
		if (num < 1 || num > arquivo.size()) {
			return null;
		}
		String string = arquivo.get(num - 1);
		return new Linha(num, string);
	}
}

class Linha {
	final int numero;
	final String string;

	Linha(int numero, String string) {
		this.numero = numero;
		this.string = string;
	}

	void processar(String str, int num, PrintWriter pw, boolean ln) {
		if (numero == num) {
			if (ln)
				pw.println(string);
			else
				pw.print(string);
		} else {
			if (ln)
				pw.println(str);
			else
				pw.print(str);
		}
	}

	@Override
	public String toString() {
		return numero + ": " + string;
	}
}