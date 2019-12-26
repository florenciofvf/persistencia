package br.com.persist.fmt;

public class FmtUtil {

	private FmtUtil() {
	}

	public static Objeto criarAtributo(String nome, String valor) {
		Objeto objeto = new Objeto();
		objeto.add(nome, new Texto(valor));

		return objeto;
	}

	public static Objeto criarAtributo(String nome, Boolean valor) {
		Objeto objeto = new Objeto();
		objeto.add(nome, new Logico(valor));

		return objeto;
	}

	public static Objeto criarAtributo(String nome, Long valor) {
		Objeto objeto = new Objeto();
		objeto.add(nome, new Numero(valor.toString()));

		return objeto;
	}

	public static Objeto criarAtributo(String nome, Double valor) {
		Objeto objeto = new Objeto();
		objeto.add(nome, new Numero(valor.toString()));

		return objeto;
	}
}