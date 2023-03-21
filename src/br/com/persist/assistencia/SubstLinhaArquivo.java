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

	private static List<Linha> getLinhas(String absoluto, String strInicio, String strFinal) throws IOException {
		Arquivo arquivo = new Arquivo(absoluto, null);
		List<Linha> lista = arquivo.getLinhasArquivo(strInicio, strFinal);
		return checkResultado(absoluto, strInicio, strFinal, lista);
	}

	private static List<Linha> checkResultado(String absoluto, String strInicio, String strFinal, List<Linha> lista)
			throws IOException {
		if (lista.isEmpty()) {
			throw new IOException("Nenhuma linha come\u00E7ando com <<<[" + strInicio + "]>>> e finalizando com <<<["
					+ strFinal + "]>>> no arquivo <<<[" + absoluto + "]>>>");
		}
		return lista;
	}

	private static List<Linha> getLinhas(String absoluto, String string) throws IOException {
		Arquivo arquivo = new Arquivo(absoluto, null);
		List<Linha> lista = arquivo.getLinhasArquivo(string);
		return checkResultado(absoluto, string, lista);
	}

	private static List<Linha> checkResultado(String absoluto, String string, List<Linha> lista) throws IOException {
		if (lista.isEmpty()) {
			throw new IOException("Nenhuma linha contendo <<<[" + string + "]>>> no arquivo <<<[" + absoluto + "]>>>");
		}
		return lista;
	}

	public static Linha getLinha(String absoluto, int num) throws IOException {
		Arquivo arquivo = new Arquivo(absoluto, null);
		return arquivo.getLinhaArquivo(num);
	}

	private static String getVersao(String absoluto) throws IOException {
		String string = getLinhas(absoluto, "<version>", "</version>").get(0).string;
		int posIni = string.indexOf('>');
		int posFim = string.indexOf("</");
		return string.substring(posIni + 1, posFim);
	}

	private static String trocarVersao(String string, String novaVersao) {
		int posIni = string.indexOf('>');
		int posFim = string.indexOf("</");
		return string.substring(0, posIni + 1) + novaVersao + string.substring(posFim);
	}

	private static void atualizarVersao(String absoluto, String tag, String novaVersao) throws IOException {
		Linha linha = getLinhas(absoluto, "<" + tag + ">", "</" + tag + ">").get(0);
		String novaString = trocarVersao(linha.string, novaVersao);
		Arquivo arquivo = new Arquivo(absoluto, new Linha(linha.numero, novaString));
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

		String client = getVersao(pomFiscalizRestClient);
		String domain = getVersao(pomDomain);
		String utils = getVersao(pomUtils);

		// FISCALIZACAO-REST
		atualizarVersao(pomFiscalizRest, "siproquim-fiscalizacao-client.version", client);
		atualizarVersao(pomFiscalizRest, "siproquim-domain.version", domain);
		atualizarVersao(pomFiscalizRest, "siproquim-utils.version", utils);

		// OFFLINE
		atualizarVersao(pomOffline, "version.siproquim-fiscalizacao-client", client);
		atualizarVersao(pomOffline, "version.siproquim-common-utils", utils);
		corrigirBuildDeployLocal(pomOffline);

		// FISCALIZACAO-REST-CLIENT
		atualizarVersao(pomFiscalizRestClient, "siproquim-utils.version", utils);

		// MAPAS-BATCH
		atualizarVersao(pomMapasBatch, "version.common.domain", domain);
		atualizarVersao(pomMapasBatch, "version.common.utils", utils);

		// MAPAS-REST
		atualizarVersao(pomMapasRest, "version.common.domain", domain);
		atualizarVersao(pomMapasRest, "version.common.utils", utils);

		// CADASTRO-REST
		atualizarVersao(pomCadastro, "version.common.domain", domain);

		// UTILS
		atualizarVersao(pomUtils, "version.siproquim-domain", domain);
	}

	private static void corrigirBuildDeployLocal(String pomOffline) throws IOException {
		Linha linha = getLinhas(pomOffline, "<exclusions>").get(0);
		substituirLinha(pomOffline, linha.numero, check(linha.string, "<!--", true));
		linha = getLinhas(pomOffline, "</exclusions>").get(0);
		substituirLinha(pomOffline, linha.numero, check(linha.string, "-->", false));
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

	List<Linha> getLinhasArquivo(String strInicio, String strFinal) throws IOException {
		List<Linha> resposta = new ArrayList<>();
		if (isEmpty(strInicio) || isEmpty(strFinal)) {
			return resposta;
		}
		strInicio = strInicio.trim();
		strFinal = strFinal.trim();
		List<String> arquivo = lerArquivo();
		for (int i = 0; i < arquivo.size(); i++) {
			String string = arquivo.get(i).trim();
			if (string.startsWith(strInicio) && string.endsWith(strFinal)) {
				resposta.add(new Linha(i + 1, arquivo.get(i)));
			}
		}
		return resposta;
	}

	List<Linha> getLinhasArquivo(String string) throws IOException {
		List<Linha> resposta = new ArrayList<>();
		if (isEmpty(string)) {
			return resposta;
		}
		string = string.trim();
		List<String> arquivo = lerArquivo();
		for (int i = 0; i < arquivo.size(); i++) {
			String str = arquivo.get(i).trim();
			if (str.equals(string)) {
				resposta.add(new Linha(i + 1, arquivo.get(i)));
			}
		}
		return resposta;
	}

	Linha getLinhaArquivo(int num) throws IOException {
		List<String> arquivo = lerArquivo();
		if (num < 1 || num > arquivo.size()) {
			return null;
		}
		String string = arquivo.get(num - 1);
		return new Linha(num, string);
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