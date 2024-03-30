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
		publicConstant(pw,
				"String LABEL_" + config.nomeCaixaAlta + "_MIN = \"label." + config.nomeCaixaBaixa + "_min\"");
		publicConstant(pw, "String PAINEL_" + config.nomeCaixaAlta + " = \"PAINEL " + config.nomeCaixaAlta + "\"");
		publicConstant(pw, "String LABEL_" + config.nomeCaixaAlta + " = \"label." + config.nomeCaixaBaixa + "\"");
		publicConstant(pw, "String " + config.nomeCaixaAlta + " = \"" + config.nomeCaixaBaixa + "\"");

		if (!Util.isEmpty(config.recurso) && !config.recurso.equals(config.nomeCaixaAlta)) {
			publicConstant(pw, "String " + config.recurso + " = \"" + config.recurso.toLowerCase() + "\"");
		}

		publicConstant(pw, "String IGNORADOS = \"ignorados\"");
		pw.println();

		privateMethod(pw, config.nomeCapitalizado + objeto + "()");
		finalMethod(pw, false);
	}
}