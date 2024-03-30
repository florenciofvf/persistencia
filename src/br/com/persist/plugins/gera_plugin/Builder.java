package br.com.persist.plugins.gera_plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

public abstract class Builder {
	protected final String objeto;
	protected final Config config;

	protected Builder(String objeto, Config config) {
		this.objeto = Objects.requireNonNull(objeto);
		this.config = Objects.requireNonNull(config);
	}

	public void gerar() throws IOException {
		File file = new File(config.diretorioDestino, config.nomeCapitalizado + objeto + ".java");
		try (PrintWriter pw = new PrintWriter(file)) {
			pw.println("package " + config.pacote + ";");
			pw.println();
			templateImport(pw);
			pw.println("public class " + config.nomeCapitalizado + objeto + " {");
			templateClass(pw);
			pw.print("}");
		}
	}

	void templateImport(PrintWriter pw) throws IOException {
	}

	abstract void templateClass(PrintWriter pw) throws IOException;
}