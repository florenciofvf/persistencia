package br.com.persist.plugins.instrucao.nat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import br.com.persist.assistencia.ArquivoString;
import br.com.persist.assistencia.LinhaString;
import br.com.persist.assistencia.Lista;

public class Arquivo {
	private Arquivo() {
	}

	public static ArquivoString criarArquivoString(Object absoluto) {
		try {
			String arquivo = absoluto.toString();
			checarArquivo(arquivo);
			Lista lista = new Lista();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo)))) {
				String string = br.readLine();
				long numero = 0;
				while (string != null) {
					lista.add(new LinhaString(++numero, string));
					string = br.readLine();
				}
			}
			return new ArquivoString(arquivo, lista);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	public static LinhaString criarLinhaString(Object numero, Object string) {
		return new LinhaString(((Number) numero).longValue(), (String) string);
	}

	public static Lista linhaPelaString(Object arquivo, Object objString) {
		ArquivoString arquivoString = (ArquivoString) arquivo;
		String string = (String) objString;
		Lista resposta = new Lista();
		Lista lista = arquivoString.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			LinhaString linhaString = (LinhaString) lista.get(i);
			if (linhaString.stringEqual(string)) {
				resposta.add(linhaString);
			}
		}
		return resposta;
	}

	public static Lista linhaPelaStringIniFim(Object arquivo, Object objIni, Object objFim) {
		ArquivoString arquivoString = (ArquivoString) arquivo;
		String strInicio = (String) objIni;
		String strFinal = (String) objFim;
		Lista resposta = new Lista();
		Lista lista = arquivoString.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			LinhaString linhaString = (LinhaString) lista.get(i);
			if (linhaString.iniciaEfinalizaCom(strInicio, strFinal)) {
				resposta.add(linhaString);
			}
		}
		return resposta;
	}

	public static Lista linhaEntrePelaStringIniFim(Object arquivo, Object objIni, Object objFim) {
		ArquivoString arquivoString = (ArquivoString) arquivo;
		String strInicio = (String) objIni;
		String strFinal = (String) objFim;
		Lista resposta = new Lista();
		Lista lista = arquivoString.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			LinhaString linhaString = (LinhaString) lista.get(i);
			String stringEntre = linhaString.stringEntre(strInicio, strFinal);
			if (stringEntre != null) {
				resposta.add(new LinhaString(linhaString.getNumero(), stringEntre));
			}
		}
		return resposta;
	}

	public static Lista linhaEntrePelaStringIniFimReplace(Object arquivo, Object objIni, Object objFim,
			Object objNova) {
		ArquivoString arquivoString = (ArquivoString) arquivo;
		String strInicio = (String) objIni;
		String strFinal = (String) objFim;
		String strNova = (String) objNova;
		Lista resposta = new Lista();
		Lista lista = arquivoString.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			LinhaString linhaString = (LinhaString) lista.get(i);
			String stringEntreReplace = linhaString.stringEntreReplace(strInicio, strFinal, strNova);
			if (stringEntreReplace != null) {
				resposta.add(new LinhaString(linhaString.getNumero(), stringEntreReplace));
			}
		}
		return resposta;
	}

	public static LinhaString substituirLinhaString(Object arquivo, Object linha) {
		ArquivoString arquivoString = (ArquivoString) arquivo;
		LinhaString linhaString = (LinhaString) linha;
		try {
			char c = ultimo(arquivoString);
			PrintWriter pw = criarPrintWriter(arquivoString);
			Lista lista = arquivoString.getLista();
			long size = lista.size().longValue();
			for (long i = 0, num = 1; i < size; i++, num++) {
				String string = ((LinhaString) lista.get(i)).getString();
				linhaString.print(pw, string, num, num < size);
			}
			if (c == '\r' || c == '\n') {
				pw.print(c);
			}
			pw.close();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return linhaString;
	}

	private static char ultimo(ArquivoString arquivoString) throws IOException {
		String absoluto = arquivoString.getAbsoluto();
		checarArquivo(absoluto);
		try (RandomAccessFile raf = new RandomAccessFile(absoluto, "r")) {
			long length = raf.length();
			raf.seek(length - 1);
			return (char) raf.read();
		}
	}

	private static PrintWriter criarPrintWriter(ArquivoString arquivoString) throws FileNotFoundException {
		return new PrintWriter(arquivoString.getAbsoluto());
	}

	private static void checarArquivo(String absoluto) throws IOException {
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