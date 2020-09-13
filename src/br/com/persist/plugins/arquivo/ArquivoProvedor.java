package br.com.persist.plugins.arquivo;

import java.io.File;

import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public class ArquivoProvedor {
	private ArquivoProvedor() {
	}

	public static String criarStringPersistencia(File file) {
		if (file == null) {
			return Constantes.VAZIO;
		}

		return montarAbsolutoRelativo(ArquivoModelo.FILE, file);
	}

	private static String montarAbsolutoRelativo(File diretorioArquivos, File file) {
		String absolutoDiretorioArquivos = diretorioArquivos.getAbsolutePath();
		String absolutoFile = file.getAbsolutePath();
		String nomeArquivo = file.getName();

		int pos = absolutoFile.indexOf(absolutoDiretorioArquivos);

		if (pos != -1) {
			String restante = absolutoFile.substring(pos + absolutoDiretorioArquivos.length());
			return Util.replaceAll(restante, Constantes.SEPARADOR, Constantes.SEP);

		} else if (nomeArquivo.startsWith(Constantes.III)) {
			return nomeArquivo;
		}

		return absolutoFile;
	}

	public static File restaurarStringPersistencia(String stringPersistencia) {
		if (Util.estaVazio(stringPersistencia)) {
			return null;
		}

		if (stringPersistencia.startsWith(Constantes.SEP)) {
			stringPersistencia = Util.replaceAll(stringPersistencia, Constantes.SEP, Constantes.SEPARADOR);
			return new File(ArquivoModelo.FILE.getAbsolutePath() + stringPersistencia);
		}

		return new File(stringPersistencia);
	}
}