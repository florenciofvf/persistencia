package br.com.persist.data;

public abstract class Tipo {
	Tipo pai;

	public static String citar(String s) {
		return "\"" + s + "\"";
	}
}