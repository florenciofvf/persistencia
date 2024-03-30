package br.com.persist.plugins.gera_plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

public abstract class Builder {
	protected final String extende;
	protected final String objeto;
	protected final Config config;

	protected Builder(String objeto, String extende, Config config) {
		this.extende = extende == null ? "" : " " + extende;
		this.objeto = Objects.requireNonNull(objeto);
		this.config = Objects.requireNonNull(config);
	}

	protected Builder(String objeto, Config config) {
		this(objeto, null, config);
	}

	public void gerar() throws IOException {
		File file = new File(config.diretorioDestino, config.nameCap + objeto + ".java");
		try (PrintWriter pw = new PrintWriter(file)) {
			pw.println("package " + config.pacote + ";");
			pw.println();
			templateImport(pw);
			pw.println("public class " + config.nameCap + objeto + extende + " {");
			templateClass(pw);
			pw.print("}");
		}
	}

	void templateImport(PrintWriter pw) throws IOException {
	}

	abstract void templateClass(PrintWriter pw) throws IOException;

	void override(String tab, PrintWriter pw) {
		pw.println(tab + "\t@Override");
	}

	void override(PrintWriter pw) {
		override("", pw);
	}

	void importar(PrintWriter pw, String string) {
		pw.println("import " + string + ";");
	}

	void publicConstant(PrintWriter pw, String string) {
		pw.println("\tpublic static final " + string + ";");
	}

	void publicMethod(String tab, PrintWriter pw, String string) {
		pw.println(tab + "\tpublic " + string + " {");
	}

	void publicMethod(PrintWriter pw, String string) {
		publicMethod("", pw, string);
	}

	void privateMethod(String tab, PrintWriter pw, String string) {
		pw.println(tab + "\tprivate " + string + " {");
	}

	void privateMethod(PrintWriter pw, String string) {
		privateMethod("", pw, string);
	}

	void privateClass(PrintWriter pw, String string) {
		pw.println("\tprivate class " + string + " {");
	}

	void returnMethod(String tab, PrintWriter pw, String string) {
		pw.println(tab + "\t\treturn " + string + ";");
	}

	void returnMethod(PrintWriter pw, String string) {
		returnMethod("", pw, string);
	}

	void instrucao(String tab, PrintWriter pw, String string) {
		pw.println(tab + "\t\t" + string + ";");
	}

	void instrucao(PrintWriter pw, String string) {
		instrucao("", pw, string);
	}

	void fragmento(String tab, PrintWriter pw, String string) {
		pw.println(tab + string);
	}

	void finalMethod(String tab, PrintWriter pw, boolean nl) {
		pw.println(tab + "\t}");
		if (nl) {
			pw.println();
		}
	}

	void finalMethod(PrintWriter pw, boolean nl) {
		finalMethod("", pw, nl);
	}

	void finalMethod(String tab, PrintWriter pw) {
		finalMethod(tab, pw, true);
	}

	void finalMethod(PrintWriter pw) {
		finalMethod("", pw, true);
	}
}