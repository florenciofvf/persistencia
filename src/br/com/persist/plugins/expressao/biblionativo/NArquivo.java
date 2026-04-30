package br.com.persist.plugins.expressao.biblionativo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.channels.FileChannel;

import br.com.persist.assistencia.Util;

public class NArquivo {
	private NArquivo() {
	}

	@Biblio(0)
	public static String getSeparador() {
		return File.separator;
	}

	@Biblio(1)
	public static BigInteger existe(Object absoluto) {
		if (absoluto == null) {
			return NUtil.FALSE;
		}
		File file = new File(absoluto.toString());
		return file.exists() ? NUtil.TRUE : NUtil.FALSE;
	}

	@Biblio(2)
	public static Lista getLinhas(Object arquivo) {
		Arquivo objArquivo = (Arquivo) arquivo;
		return objArquivo.getLista();
	}

	@Biblio(3)
	public static Arquivo criarArquivo(Object absoluto) throws IOException {
		String arquivo = absoluto.toString();
		checarAbsoluto(arquivo);
		try (InputStream is = new FileInputStream(arquivo)) {
			Lista lista = new Lista();
			long numero = 1;
			Linha linha = criarLinha(numero, is);
			while (linha != null) {
				lista.add(linha);
				numero++;
				linha = criarLinha(numero, is);
			}
			return new Arquivo(new File(arquivo), lista);
		}
	}

	private static Linha criarLinha(long numero, InputStream is) throws IOException {
		StringBuilder builder = new StringBuilder();
		int i = is.read();
		while (i != -1) {
			char c = (char) i;
			builder.append(c);
			if (c == '\n') {
				return new Linha(numero, builder.toString());
			}
			i = is.read();
		}
		if (builder.length() > 0) {
			return new Linha(numero, builder.toString());
		}
		return null;
	}

	@Biblio(4)
	public static void salvarArquivo(Object arquivo, Object charset)
			throws FileNotFoundException, UnsupportedEncodingException, IllegalAccessException {
		Arquivo objArquivo = (Arquivo) arquivo;
		PrintWriter pw = criarPrintWriter(objArquivo, (String) charset);
		objArquivo.salvar(pw);
		pw.close();
	}

	private static PrintWriter criarPrintWriter(Arquivo arquivo, String charset)
			throws FileNotFoundException, UnsupportedEncodingException {
		return new PrintWriter(arquivo.getFile(), charset);
	}

	@Biblio(5)
	public static void salvarLinhas(Object absoluto, Object lista, Object charset)
			throws FileNotFoundException, UnsupportedEncodingException, IllegalAccessException {
		Arquivo arquivo = new Arquivo(new File(absoluto.toString()), (Lista) lista);
		salvarArquivo(arquivo, charset);
	}

	@Biblio(6)
	public static Reader criarStringReader(Object absoluto) throws IOException {
		StringBuilder builder = new StringBuilder();
		String arquivo = absoluto.toString();
		checarAbsoluto(arquivo);
		try (InputStream is = new FileInputStream(arquivo)) {
			byte[] bytes = Util.getArrayBytes(is);
			builder.append(new String(bytes));
		}
		return new StringReader(builder.toString());
	}

	@Biblio(7)
	public static String copiar(Object absolutoOrigem, Object absolutoDestino) throws IOException {
		if (absolutoOrigem == null) {
			return "ORIGEM NULL";
		}
		if (absolutoDestino == null) {
			return "DESTINO NULL";
		}
		File origem = new File(absolutoOrigem.toString());
		if (!origem.isFile()) {
			return "ORIGEM NAO EH ARQUIVO";
		}
		if (!origem.canRead()) {
			return "ORIGEM NAO PODE SER LIDO";
		}
		File destino = new File(absolutoDestino.toString());
		try (FileInputStream fis = new FileInputStream(origem)) {
			try (FileOutputStream fos = new FileOutputStream(destino)) {
				FileChannel channelIn = fis.getChannel();
				FileChannel channelOut = fos.getChannel();
				return destino.getAbsolutePath() + "\nTOTAL COPIADO(s): "
						+ channelIn.transferTo(0, origem.length(), channelOut);
			}
		}
	}

	private static void checarAbsoluto(String absoluto) throws IOException {
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