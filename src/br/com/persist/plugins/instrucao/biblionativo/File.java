package br.com.persist.plugins.instrucao.biblionativo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;

public class File {
	private File() {
	}

	@Biblio
	public static Arquivo criarArquivo(Object absoluto) {
		try {
			java.lang.String arquivo = absoluto.toString();
			checarAbsoluto(arquivo);
			try (InputStream is = new FileInputStream(arquivo)) {
				Lista lista = new Lista();
				long numero = 1;
				ArquivoLinha linha = criar(numero, is);
				while (linha != null) {
					lista.add(linha);
					numero++;
					linha = criar(numero, is);
				}
				return new Arquivo(arquivo, lista);
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	private static ArquivoLinha criar(long numero, InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		ArquivoLinha resposta = null;
		int i = is.read();
		char cr = 0;
		char lf = 0;
		while (i != -1) {
			char c = (char) i;
			if (c == '\r' || c == '\n') {
				if (c == '\r') {
					cr = c;
				} else {
					lf = c;
					return new ArquivoLinha(numero, sb.toString(), cr, lf);
				}
			} else {
				sb.append(c);
			}
			i = is.read();
		}
		if (cr != 0) {
			resposta = new ArquivoLinha(numero, sb.toString(), cr, lf);
		}
		return resposta;
	}

	@Biblio
	public static Lista selecionarLinhas(Object arquivo, Object string, Object trim) {
		Arquivo entityArquivo = (Arquivo) arquivo;
		java.lang.String str = (java.lang.String) string;
		Lista resposta = new Lista();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ArquivoLinha linha = (ArquivoLinha) lista.get(i);
			if (linha.stringEqual(str, Util.TRUE.equals(trim))) {
				resposta.add(linha);
			}
		}
		return resposta;
	}

	@Biblio
	public static Lista selecionarLinhasIniciaEfinalizaCom(Object arquivo, Object stringInicio, Object stringFinal,
			Object trim) {
		Arquivo entityArquivo = (Arquivo) arquivo;
		java.lang.String strInicio = (java.lang.String) stringInicio;
		java.lang.String strFinal = (java.lang.String) stringFinal;
		Lista resposta = new Lista();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ArquivoLinha linha = (ArquivoLinha) lista.get(i);
			if (linha.iniciaEfinalizaCom(strInicio, strFinal, Util.TRUE.equals(trim))) {
				resposta.add(linha);
			}
		}
		return resposta;
	}

	@Biblio
	public static Lista selecionarLinhasConteudoEntreIniciaEfinalizaCom(Object arquivo, Object stringInicio,
			Object stringFinal, Object trim) {
		Arquivo entityArquivo = (Arquivo) arquivo;
		java.lang.String strInicio = (java.lang.String) stringInicio;
		java.lang.String strFinal = (java.lang.String) stringFinal;
		Lista resposta = new Lista();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ArquivoLinha linha = (ArquivoLinha) lista.get(i);
			java.lang.String stringEntre = linha.stringEntre(strInicio, strFinal, Util.TRUE.equals(trim));
			if (stringEntre != null) {
				resposta.add(linha.clonar(stringEntre));
			}
		}
		return resposta;
	}

	@Biblio
	public static Lista selecionarLinhasConteudoEntreIniciaEfinalizaComReplace(Object arquivo, Object stringInicio,
			Object stringFinal, Object novaString, Object trim) {
		Arquivo entityArquivo = (Arquivo) arquivo;
		java.lang.String strInicio = (java.lang.String) stringInicio;
		java.lang.String strFinal = (java.lang.String) stringFinal;
		java.lang.String strNova = (java.lang.String) novaString;
		Lista resposta = new Lista();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ArquivoLinha linha = (ArquivoLinha) lista.get(i);
			java.lang.String stringEntreReplace = linha.stringEntreReplace(strInicio, strFinal, strNova,
					Util.TRUE.equals(trim));
			if (stringEntreReplace != null) {
				resposta.add(linha.clonar(stringEntreReplace));
			}
		}
		return resposta;
	}

	@Biblio
	public static ArquivoLinha getLinha(Object arquivo, Object numero) {
		Arquivo entityArquivo = (Arquivo) arquivo;
		long numeroLinha = ((Number) numero).longValue();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ArquivoLinha linha = (ArquivoLinha) lista.get(i);
			if (linha.numeroEqual(numeroLinha)) {
				return linha;
			}
		}
		return null;
	}

	@Biblio
	public static ArquivoLinha clonarLinha(Object linha, Object string) {
		ArquivoLinha entityLinha = (ArquivoLinha) linha;
		return entityLinha.clonar((java.lang.String) string);
	}

	@Biblio
	public static ArquivoLinha criarLinha(Object numero, Object string) {
		return new ArquivoLinha(((Number) numero).longValue(), (java.lang.String) string, (char) 0, '\n');
	}

	@Biblio
	public static ArquivoLinha substituirLinha(Object arquivo, Object linha, Object charset) {
		Arquivo entityArquivo = (Arquivo) arquivo;
		ArquivoLinha entityLinha = (ArquivoLinha) linha;
		try {
			PrintWriter pw = criarPrintWriter(entityArquivo, (java.lang.String) charset);
			Lista lista = entityArquivo.getLista();
			long size = lista.size().longValue();
			for (long i = 0, num = 1; i < size; i++, num++) {
				ArquivoLinha entity = (ArquivoLinha) lista.get(i);
				entityLinha.print(pw, entity, num);
			}
			pw.close();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return entityLinha;
	}

	@Biblio
	public static void salvarArquivo(Object arquivo, Object charset) {
		Arquivo entityArquivo = (Arquivo) arquivo;
		try {
			PrintWriter pw = criarPrintWriter(entityArquivo, (java.lang.String) charset);
			entityArquivo.salvar(pw);
			pw.close();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Biblio
	public static void setLinha(Object arquivo, Object linha) {
		Arquivo entityArquivo = (Arquivo) arquivo;
		ArquivoLinha entityLinha = (ArquivoLinha) linha;
		try {
			Lista lista = entityArquivo.getLista();
			lista.set(entityLinha.getNumero() - 1, entityLinha);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	private static PrintWriter criarPrintWriter(Arquivo arquivo, java.lang.String charset)
			throws FileNotFoundException, UnsupportedEncodingException {
		return new PrintWriter(arquivo.getAbsoluto(), charset);
	}

	private static void checarAbsoluto(java.lang.String absoluto) throws IOException {
		java.io.File file = new java.io.File(absoluto);
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

	@Biblio
	public static java.lang.String copiar(Object absolutoOrigem, Object absolutoDestino) throws IOException {
		if (absolutoOrigem == null) {
			return "ORIGEM NULL";
		}
		java.io.File origem = new java.io.File(absolutoOrigem.toString());
		if (!origem.isFile()) {
			return "ORIGEM NAO EH ARQUIVO";
		}
		if (!origem.canRead()) {
			return "ORIGEM NAO PODE SER LIDO";
		}
		if (absolutoDestino == null) {
			return "DESTINO NULL";
		}
		java.io.File destino = new java.io.File(absolutoDestino.toString());
		try (FileInputStream fis = new FileInputStream(origem)) {
			try (FileOutputStream fos = new FileOutputStream(destino)) {
				FileChannel channelIn = fis.getChannel();
				FileChannel channelOut = fos.getChannel();
				return destino.getAbsolutePath() + "\nTOTAL COPIADO(s): "
						+ channelIn.transferTo(0, origem.length(), channelOut);
			}
		}
	}
}