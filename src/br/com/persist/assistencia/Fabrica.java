package br.com.persist.assistencia;

import br.com.persist.abstrato.FabricaContainer;

public class Fabrica {
	private Fabrica() {
	}

	public static FabricaContainer criar(String classe) {
		if (classe == null || classe.trim().isEmpty()) {
			return null;
		}
		try {
			Class<?> klass = Class.forName(classe);
			return (FabricaContainer) klass.newInstance();
		} catch (Exception e) {
			return null;
		}
	}
}