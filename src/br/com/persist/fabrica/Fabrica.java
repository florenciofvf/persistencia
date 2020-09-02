package br.com.persist.fabrica;

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