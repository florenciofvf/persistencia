package br.com.persist.data;

public class ObjetoUtil {
	private ObjetoUtil() {
	}

	public static boolean contemAtributo(Tipo tipo, String nome) {
		if (tipo instanceof Objeto) {
			Objeto objeto = (Objeto) tipo;
			return objeto.getValor(nome) != null;
		}
		return false;
	}

	public static String getValorAtributo(Tipo tipo, String nome) {
		if (tipo instanceof Objeto) {
			Objeto objeto = (Objeto) tipo;
			return objeto.getValor(nome).toString();
		}
		return null;
	}
}