package br.com.persist.plugins.instrucao.biblio_nativo;

public class Colecao {
	private Colecao() {
	}

	public static Lista list() {
		return new Lista();
	}

	public static Object notContains(Object lista, Object valor) {
		return ((Lista) lista).notContains(valor);
	}

	public static Object contains(Object lista, Object valor) {
		return ((Lista) lista).contains(valor);
	}

	public static Object add(Object lista, Object valor) {
		return ((Lista) lista).add(valor);
	}

	public static Object size(Object lista) {
		return ((Lista) lista).size();
	}

	public static Object empty(Object lista) {
		return ((Lista) lista).empty();
	}

	public static Object head(Object lista) {
		return ((Lista) lista).head();
	}

	public static Object tail(Object lista) {
		return ((Lista) lista).tail();
	}

	public static Object clear(Object lista) {
		return ((Lista) lista).clear();
	}
}