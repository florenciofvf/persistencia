package br.com.persist.data;

import br.com.persist.assistencia.Util;

public class Filtro {
	private Filtro() {
	}

	public static Objeto comAtributos(Objeto objeto, String[] atributos) {
		if (!contemAtributos(objeto, atributos)) {
			return null;
		}
		objeto.filtrarComAtributos(atributos);
		return objeto;
	}

	public static Objeto semAtributos(Objeto objeto, String[] atributos) {
		if (contemAtributos(objeto, atributos)) {
			return null;
		}
		objeto.filtrarSemAtributos(atributos);
		return objeto;
	}

	public static Array comAtributos(Array array, String[] atributos) {
		array.filtrarComAtributos(atributos);
		return array;
	}

	public static Array semAtributos(Array array, String[] atributos) {
		array.filtrarSemAtributos(atributos);
		return array;
	}

	public static boolean contemAtributos(Objeto objeto, String[] atributos) {
		if (objeto == null || atributos == null || atributos.length == 0) {
			return false;
		}
		for (String string : atributos) {
			if (Util.isEmpty(string) || !objeto.contemAtributo(string)) {
				return false;
			}
		}
		return true;
	}
}