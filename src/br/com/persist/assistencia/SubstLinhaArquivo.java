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
	final String absoluto;
	final Linha linha;

	Arquivo(String absoluto, Linha linha) {
		this.absoluto = absoluto;
		this.linha = linha;
	}

	void processar() throws IOException {
		List<String> arquivo = lerArquivo();
		if (arquivo.size() < linha.numero) {
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
	}

	List<String> lerArquivo() throws IOException {
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

	char ultimo() throws IOException {
		try (RandomAccessFile raf = new RandomAccessFile(absoluto, "r")) {
			long length = raf.length();
			raf.seek(length - 1);
			return (char) raf.read();
		}
	}

	private PrintWriter criarPrintWriter() throws FileNotFoundException {
		return new PrintWriter(absoluto);
	}
}

class Linha {
	final int numero;
	final String novo;

	Linha(int numero, String novo) {
		this.numero = numero;
		this.novo = novo;
	}

	void processar(String string, int num, PrintWriter pw, boolean ln) {
		if (numero == num) {
			if (ln)
				pw.println(novo);
			else
				pw.print(novo);
		} else {
			if (ln)
				pw.println(string);
			else
				pw.print(string);
		}
	}
}