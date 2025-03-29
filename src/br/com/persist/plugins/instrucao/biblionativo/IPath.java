package br.com.persist.plugins.instrucao.biblionativo;

import java.io.File;

import br.com.persist.assistencia.Util;

public class IPath {
	private IPath() {
	}

	@Biblio(0)
	public static String get(Object object) {
		if (object == null) {
			return "";
		}
		String string = object.toString();
		if (File.separatorChar == '/') {
			string = Util.replaceAll(string, "\\", "/");
		} else if (File.separatorChar == '\\') {
			string = Util.replaceAll(string, "/", "\\");
		}
		return string;
	}

	@Biblio(1)
	public static String absolute(Object object) {
		if (object == null) {
			return "";
		}
		String string = object.toString();
		return new File(string).getAbsolutePath();
	}
}