package br.com.persist.plugins.instrucao.biblionativo;

import java.io.IOException;

public class Runtime {
	private Runtime() {
	}

	@Biblio
	public static void exec(Object comando) throws IOException {
		java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
		runtime.exec(comando.toString());
	}
}