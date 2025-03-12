package br.com.persist.plugins.gera_plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import br.com.persist.assistencia.StringPool;
import br.com.persist.geradores.Arquivo;
import br.com.persist.geradores.ClassePublica;

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
		try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
			StringPool pool = new StringPool();

			Arquivo arquivo = new Arquivo();
			arquivo.addPackage(config.pacote).newLine();
			templateImport(arquivo);

			ClassePublica classe = arquivo.criarClassePublica(config.nameCap + objeto + extende);
			templateClass(classe);

			arquivo.gerar(-1, pool);
			pw.print(pool.toString());
		}
	}

	void templateImport(Arquivo arquivo) {
	}

	abstract void templateClass(ClassePublica classe);
}