package br.com.persist.plugins.gera_plugin;

import br.com.persist.assistencia.Util;
import br.com.persist.geradores.ClassePublica;

public class ConstantesBuilder extends Builder {
	protected ConstantesBuilder(Config config) {
		super("Constantes", config);
	}

	@Override
	void templateClass(ClassePublica classe) {
		classe.addCampoConstanteString(config.nameUpperEntre("LABEL_", "_MIN"), config.nameLowerEntre("label.", "_min"));
		classe.addCampoConstanteString(config.nameUpperApos("PAINEL_"), config.nameUpperApos("PAINEL "));
		classe.addCampoConstanteString(config.nameUpperApos("LABEL_"), config.nameLowerApos("label."));
		classe.addCampoConstanteString(config.nameUpper, config.nameLower);

		if (!Util.isEmpty(config.recurso) && !config.recurso.equals(config.nameUpper)) {
			classe.addCampoConstanteString(config.recurso, config.recurso.toLowerCase());
		}

		classe.addCampoConstanteString("IGNORADOS", "ignorados").newLine();
		classe.criarConstrutorPublico(config.nameCapConstantes());
	}
}