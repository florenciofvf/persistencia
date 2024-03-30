package br.com.persist.plugins.gera_plugin;

import java.io.IOException;
import java.io.PrintWriter;

import br.com.persist.assistencia.Util;

public class ConstantesBuilder extends Builder {
	protected ConstantesBuilder(Config config) {
		super("Constantes", config);
	}

	@Override
	void templateClass(PrintWriter pw) throws IOException {
		pw.println("\tpublic static final String LABEL_" + config.nomeCaixaAlta + "_MIN = \"label."
				+ config.nomeCaixaBaixa + "_min\";");
		pw.println("\tpublic static final String PAINEL_" + config.nomeCaixaAlta + " = \"PAINEL " + config.nomeCaixaAlta
				+ "\";");
		pw.println("\tpublic static final String LABEL_" + config.nomeCaixaAlta + " = \"label." + config.nomeCaixaBaixa
				+ "\";");
		pw.println("\tpublic static final String " + config.nomeCaixaAlta + " = \"" + config.nomeCaixaBaixa + "\";");

		if (!Util.isEmpty(config.recurso) && !config.recurso.equals(config.nomeCaixaAlta)) {
			pw.println(
					"\tpublic static final String " + config.recurso + " = \"" + config.recurso.toLowerCase() + "\";");
		}

		pw.println("\tpublic static final String IGNORADOS = \"ignorados\";");
		pw.println();

		pw.println("\tprivate " + config.nomeCapitalizado + objeto + "() {");
		pw.println("\t}");
	}
}