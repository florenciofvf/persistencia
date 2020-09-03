package br.com.persist.util;

import br.com.persist.abstrato.FabricaContainer;

public class Fabrica {
	private Fabrica() {
	}

	public static FabricaContainer criar(String classe) {
		try {
			Class<?> klass = Class.forName(classe);
			return (FabricaContainer) klass.newInstance();
		} catch (Exception e) {
			return null;
		}
	}
}