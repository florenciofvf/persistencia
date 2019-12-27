package br.com.persist.fmt;

public class FmtUtil {

	private FmtUtil() {
	}

	public static Objeto criarAtributo(String nome, String valor) {
		return new Objeto().atributo(nome, valor);
	}

	public static Objeto criarAtributo(String nome, boolean valor) {
		return new Objeto().atributo(nome, valor);
	}

	public static Objeto criarAtributo(String nome, long valor) {
		return new Objeto().atributo(nome, valor);
	}

	public static Objeto criarAtributo(String nome, double valor) {
		return new Objeto().atributo(nome, valor);
	}
}