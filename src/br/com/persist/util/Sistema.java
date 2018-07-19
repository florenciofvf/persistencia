package br.com.persist.util;

public class Sistema {
	private static final Sistema sistema = new Sistema();
	private final boolean mac;

	public Sistema() {
		String s = System.getProperty("os.name");
		mac = s != null && s.startsWith("Mac OS");
	}

	public static Sistema getInstancia() {
		return sistema;
	}

	public boolean isMac() {
		return mac;
	}
}