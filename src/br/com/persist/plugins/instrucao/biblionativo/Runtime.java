package br.com.persist.plugins.instrucao.biblionativo;

public class Runtime {
	private Runtime() {
	}

	@Biblio
	public static void exec(Object comando) {
		try {
			java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
			runtime.exec(comando.toString());
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
}