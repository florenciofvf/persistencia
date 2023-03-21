package br.com.persist.assistencia;

import java.io.BufferedReader;
import java.io.File;
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
		processar();
	}

	private static String trocarVersao(String string, String novaVersao) {
		int posIni = string.indexOf('>');
		int posFim = string.indexOf("</");
		return string.substring(0, posIni + 1) + novaVersao + string.substring(posFim);
	}

	private static String getVersao(String absoluto) throws IOException {
		String string = getLinha(absoluto, "<version>", "</version>");
		int posIni = string.indexOf('>');
		int posFim = string.indexOf("</");
		return string.substring(posIni + 1, posFim);
	}

	private static String getLinha(String absoluto, String strInicio, String strFinal) throws IOException {
		Arquivo arquivo = new Arquivo(absoluto, null);
		return arquivo.getLinhaArquivo(strInicio, strFinal).string;
	}

	private static String getLinha(String absoluto, int num) throws IOException {
		Arquivo arquivo = new Arquivo(absoluto, null);
		return arquivo.getLinhaArquivo(num).string;
	}

	private static void substituirVersao(String absoluto, int num, String novaVersao) throws IOException {
		String string = getLinha(absoluto, num);
		String novaString = trocarVersao(string, novaVersao);
		Arquivo arquivo = new Arquivo(absoluto, new Linha(num, novaString));
		arquivo.processar();
	}

	public static void substituirLinha(String absoluto, int num, String novaString) throws IOException {
		Arquivo arquivo = new Arquivo(absoluto, new Linha(num, novaString));
		arquivo.processar();
	}

	private static void processar() throws IOException {
		String pomFiscalizRestClient = "/Users/florenciovieirafilho/desenv/projetos/proad/siproquim-fiscalizacao-rest-client/pom.xml";
		String pomFiscalizRest = "/Users/florenciovieirafilho/desenv/projetos/proad/siproquim-fiscalizacao-rest/pom.xml";
		String pomOffline = "/Users/florenciovieirafilho/desenv/projetos/proad/siproquim-fiscalizacao-offline/pom.xml";
		String pomDomain = "/Users/florenciovieirafilho/desenv/projetos/siproquim/siproquim-common-domain/pom.xml";
		String pomMapasBatch = "/Users/florenciovieirafilho/desenv/projetos/mapa/siproquim-mapas-batch/pom.xml";
		String pomMapasRest = "/Users/florenciovieirafilho/desenv/projetos/mapa/siproquim-mapas-rest/pom.xml";
		String pomCadastro = "/Users/florenciovieirafilho/desenv/projetos/siproquim/siproquim-rest/pom.xml";
		String pomUtils = "/Users/florenciovieirafilho/desenv/projetos/siproquim-common-utils/pom.xml";

		String versaoClient = getVersao(pomFiscalizRestClient);
		String versaoDomain = getVersao(pomDomain);
		String versaoUtils = getVersao(pomUtils);

		// FISCALIZACAO-REST-CLIENT
		substituirVersao(pomFiscalizRestClient, 14, versaoUtils);

		// FISCALIZACAO-REST
		substituirVersao(pomFiscalizRest, 28, versaoDomain);
		substituirVersao(pomFiscalizRest, 29, versaoUtils);
		substituirVersao(pomFiscalizRest, 30, versaoClient);

		// MAPAS-BATCH
		substituirVersao(pomMapasBatch, 19, versaoDomain);
		substituirVersao(pomMapasBatch, 20, versaoUtils);

		// MAPAS-REST
		substituirVersao(pomMapasRest, 21, versaoDomain);
		substituirVersao(pomMapasRest, 22, versaoUtils);

		// CADASTRO-REST
		substituirVersao(pomCadastro, 26, versaoDomain);

		// OFFLINE
		substituirVersao(pomOffline, 40, versaoClient);
		substituirVersao(pomOffline, 41, versaoUtils);
		String string = getLinha(pomOffline, 97);
		substituirLinha(pomOffline, 97, check(string, "<!--", true));
		string = getLinha(pomOffline, 102);
		substituirLinha(pomOffline, 102, check(string, "-->", false));

		// UTILS
		substituirVersao(pomUtils, 18, versaoDomain);
	}

	static String check(String string, String delta, boolean inicio) {
		if (inicio) {
			return string.startsWith(delta) ? string : delta + string;
		}
		return string.endsWith(delta) ? string : string + delta;
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
		checarArquivo();
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
		checarArquivo();
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

	Linha getLinhaArquivo(String strInicio, String strFinal) throws IOException {
		if (isEmpty(strInicio) || isEmpty(strFinal)) {
			return null;
		}
		strInicio = strInicio.trim();
		strFinal = strFinal.trim();
		List<String> arquivo = lerArquivo();
		for (int i = 0; i < arquivo.size(); i++) {
			String string = arquivo.get(i).trim();
			if (string.startsWith(strInicio) && string.endsWith(strFinal)) {
				return new Linha(i + 1, arquivo.get(i));
			}
		}
		return null;
	}

	private boolean isEmpty(String string) {
		return string == null || string.trim().isEmpty();
	}

	private void checarArquivo() throws IOException {
		File file = new File(absoluto);
		if (!file.exists()) {
			throw new IOException("Arquivo inexistente! >>> " + absoluto);
		}
		if (!file.canRead()) {
			throw new IOException("O arquivo n\u00e3o pode ser lido! >>> " + absoluto);
		}
		if (file.isDirectory()) {
			throw new IOException("O arquivo n\u00e3o pode ser um diret\u00F3rio! >>> " + absoluto);
		}
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