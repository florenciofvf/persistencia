package br.com.persist.plugins.instrucao.nat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

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
			try (InputStream is = new FileInputStream(arquivo)) {
				Lista lista = new Lista();
				long numero = 1;
				LinhaString linhaString = criar(numero, is);
				while (linhaString != null) {
					lista.add(linhaString);
					numero++;
					linhaString = criar(numero, is);
				}
				return new ArquivoString(arquivo, lista);
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	private static LinhaString criar(long numero, InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		LinhaString resposta = null;
		char cr = (char) 0;
		char lf = (char) 0;
		int i = is.read();
		while (i != -1) {
			char c = (char) i;
			if (c == '\r' || c == '\n') {
				if (c == '\r') {
					cr = c;
				} else {
					lf = c;
					return new LinhaString(numero, sb.toString(), cr, lf);
				}
			} else {
				sb.append(c);
			}
			i = is.read();
		}
		if (cr != 0) {
			resposta = new LinhaString(numero, sb.toString(), cr, lf);
		}
		return resposta;
	}

	public static Lista selecionarLinhaString(Object arquivo, Object objString) {
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

	public static Lista selecionarLinhaStringIniFim(Object arquivo, Object objIni, Object objFim) {
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

	public static Lista selecionarLinhaStringEntreIniFim(Object arquivo, Object objIni, Object objFim) {
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
				resposta.add(linhaString.clonar(stringEntre));
			}
		}
		return resposta;
	}

	public static Lista selecionarLinhaStringEntreIniFimReplace(Object arquivo, Object objIni, Object objFim,
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
				resposta.add(linhaString.clonar(stringEntreReplace));
			}
		}
		return resposta;
	}

	public static LinhaString getLinhaString(Object arquivo, Object numero) {
		ArquivoString arquivoString = (ArquivoString) arquivo;
		long numeroLinha = ((Number) numero).longValue();
		Lista lista = arquivoString.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			LinhaString linhaString = (LinhaString) lista.get(i);
			if (linhaString.numeroEqual(numeroLinha)) {
				return linhaString;
			}
		}
		return null;
	}

	public static LinhaString clonarLinhaString(Object linha, Object string) {
		LinhaString linhaString = (LinhaString) linha;
		return linhaString.clonar((String) string);
	}

	public static LinhaString criarLinhaString(Object numero, Object string) {
		return new LinhaString(((Number) numero).longValue(), (String) string, (char) 0, '\n');
	}

	public static LinhaString substituirLinhaString(Object arquivo, Object linha) {
		ArquivoString arquivoString = (ArquivoString) arquivo;
		LinhaString linhaString = (LinhaString) linha;
		try {
			PrintWriter pw = criarPrintWriter(arquivoString);
			Lista lista = arquivoString.getLista();
			long size = lista.size().longValue();
			for (long i = 0, num = 1; i < size; i++, num++) {
				LinhaString ls = (LinhaString) lista.get(i);
				linhaString.print(pw, ls, num);
			}
			pw.close();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return linhaString;
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