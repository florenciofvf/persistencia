package br.com.persist.plugins.instrucao.biblionativo;

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

public class IArquivo {
	private IArquivo() {
	}

	@Biblio(3)
	public static Arquivo ler(Object absoluto) throws IOException {
		String arquivo = absoluto.toString();
		checarAbsoluto(arquivo);
		try (InputStream is = new FileInputStream(arquivo)) {
			Lista lista = new Lista();
			long numero = 1;
			Linha linha = criar(numero, is);
			while (linha != null) {
				lista.add(linha);
				numero++;
				linha = criar(numero, is);
			}
			return new Arquivo(arquivo, lista);
		}
	}

	private static Linha criar(long numero, InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		int i = is.read();
		while (i != -1) {
			char c = (char) i;
			sb.append(c);
			if (c == '\n') {
				return new Linha(numero, sb.toString());
			}
			i = is.read();
		}
		if (sb.length() > 0) {
			return new Linha(numero, sb.toString());
		}
		return null;
	}

	@Biblio(1)
	public static Lista getLista(Object arquivo) {
		Arquivo entityArquivo = (Arquivo) arquivo;
		return entityArquivo.getLista();
	}

	@Biblio(2)
	public static void salvar(Object arquivo, Object charset)
			throws FileNotFoundException, UnsupportedEncodingException, IllegalAccessException {
		Arquivo entityArquivo = (Arquivo) arquivo;
		PrintWriter pw = criarPrintWriter(entityArquivo, (String) charset);
		entityArquivo.salvar(pw);
		pw.close();
	}

	@Biblio(4)
	public static void salvarLinhas(Object absoluto, Object lista, Object charset)
			throws FileNotFoundException, UnsupportedEncodingException, IllegalAccessException {
		Arquivo arquivo = new Arquivo(absoluto.toString(), (Lista) lista);
		salvar(arquivo, charset);
	}

	private static PrintWriter criarPrintWriter(Arquivo arquivo, String charset)
			throws FileNotFoundException, UnsupportedEncodingException {
		return new PrintWriter(arquivo.getAbsoluto(), charset);
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

	@Biblio(0)
	public static String copiar(Object absolutoOrigem, Object absolutoDestino) throws IOException {
		if (absolutoOrigem == null) {
			return "ORIGEM NULL";
		}
		File origem = new File(absolutoOrigem.toString());
		if (!origem.isFile()) {
			return "ORIGEM NAO EH ARQUIVO";
		}
		if (!origem.canRead()) {
			return "ORIGEM NAO PODE SER LIDO";
		}
		if (absolutoDestino == null) {
			return "DESTINO NULL";
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

	@Biblio(5)
	public static BigInteger existe(Object absoluto) {
		if (absoluto == null) {
			return IUtil.FALSE;
		}
		File file = new File(absoluto.toString());
		return file.exists() ? IUtil.TRUE : IUtil.FALSE;
	}

	@Biblio(6)
	public static String separador() {
		return File.separator;
	}

	@Biblio(7)
	public static Reader createStringReader(Object absoluto) throws IOException {
		StringBuilder sb = new StringBuilder();
		String arquivo = absoluto.toString();
		checarAbsoluto(arquivo);
		try (InputStream is = new FileInputStream(arquivo)) {
			byte[] bytes = Util.getArrayBytes(is);
			sb.append(new String(bytes));
		}
		return new StringReader(sb.toString());
	}
}