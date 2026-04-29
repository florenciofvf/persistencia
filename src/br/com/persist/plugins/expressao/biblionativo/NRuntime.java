package br.com.persist.plugins.expressao.biblionativo;

import java.io.IOException;

public class NRuntime {
	private NRuntime() {
	}

	@Biblio
	public static void exec(Object comando) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		runtime.exec(comando.toString());
	}
}