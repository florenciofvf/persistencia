package br.com.persist.plugins.objeto.vinculo;

import java.io.File;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class ArquivoVinculo {
	private final String arquivo;

	public ArquivoVinculo(String arquivo) {
		this.arquivo = arquivo == null ? "" : arquivo;
	}

	public File getFile() {
		String string = Util.replaceAll(arquivo, Constantes.SEP, Constantes.SEPARADOR);
		return new File(string);
	}

	private boolean comDiretorio() {
		return arquivo.contains("/") || arquivo.contains("\\");
	}

	public boolean valido() {
		return !Util.isEmpty(arquivo);
	}

	public void checarDiretorio() {
		if (comDiretorio()) {
			int pos = arquivo.lastIndexOf(Constantes.SEPARADOR);
			String diretorio = arquivo.substring(pos);
			File file = new File(diretorio);
			if (!file.isDirectory()) {
				file.mkdir();
			}
		}
	}

	public String getArquivo() {
		return arquivo;
	}
}