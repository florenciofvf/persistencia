package br.com.persist.plugins.objeto;

import java.io.File;

public class ObjetoProvedor {
	private static File parentFile;

	private ObjetoProvedor() {
	}

	public static File getParentFile() {
		return parentFile;
	}

	public static void setParentFile(File parentFile) {
		ObjetoProvedor.parentFile = parentFile;
	}
}