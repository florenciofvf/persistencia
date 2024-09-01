package br.com.persist.plugins.instrucao.biblionativo;

import java.io.IOException;

public class IRuntime {
	private IRuntime() {
	}

	@Biblio
	public static void exec(Object comando) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		runtime.exec(comando.toString());
	}
}