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
		publicConstant(pw, "String LABEL_" + config.nameUpper + "_MIN = \"label." + config.nameLower + "_min\"");
		publicConstant(pw, "String PAINEL_" + config.nameUpper + " = \"PAINEL " + config.nameUpper + "\"");
		publicConstant(pw, "String LABEL_" + config.nameUpper + " = \"label." + config.nameLower + "\"");
		publicConstant(pw, "String " + config.nameUpper + " = \"" + config.nameLower + "\"");

		if (!Util.isEmpty(config.recurso) && !config.recurso.equals(config.nameUpper)) {
			publicConstant(pw, "String " + config.recurso + " = \"" + config.recurso.toLowerCase() + "\"");
		}

		publicConstant(pw, "String IGNORADOS = \"ignorados\"");
		pw.println();

		privateMethod(pw, config.nameCap + objeto + "()");
		finalMethod(pw, false);
	}
}