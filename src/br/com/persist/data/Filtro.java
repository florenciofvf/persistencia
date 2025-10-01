package br.com.persist.data;

import br.com.persist.assistencia.Util;

public class Filtro {
	private Filtro() {
	}

	public static Objeto comAtributos(Objeto objeto, String[] atributos, String valorAtributo) {
		if (!contemAtributos(objeto, atributos, valorAtributo)) {
			return null;
		}
		objeto.filtrarComAtributos(atributos, valorAtributo);
		return objeto;
	}

	public static Objeto semAtributos(Objeto objeto, String[] atributos) {
		if (contemAtributos(objeto, atributos, null)) {
			return null;
		}
		objeto.filtrarSemAtributos(atributos);
		return objeto;
	}

	public static Array comAtributos(Array array, String[] atributos, String valorAtributo) {
		array.filtrarComAtributos(atributos, valorAtributo);
		return array;
	}

	public static Array semAtributos(Array array, String[] atributos) {
		array.filtrarSemAtributos(atributos);
		return array;
	}

	public static boolean contemAtributos(Objeto objeto, String[] atributos, String valorAtributo) {
		if (objeto == null || atributos == null || atributos.length == 0) {
			return false;
		}
		for (String item : atributos) {
			if (Util.isEmpty(item)) {
				return false;
			}
			NomeValor atributo = objeto.getAtributo(item);
			if (atributo == null) {
				return false;
			}
			if (!Util.isEmpty(valorAtributo) && !atributo.contemValor(valorAtributo)) {
				return false;
			}
		}
		return true;
	}
}