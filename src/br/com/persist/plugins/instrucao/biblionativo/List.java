package br.com.persist.plugins.instrucao.biblionativo;

public class List {
	private List() {
	}

	public static Lista create() {
		return new Lista();
	}

	public static Object notContains(Object lista, Object valor) {
		return ((Lista) lista).notContains(valor);
	}

	public static Object contains(Object lista, Object valor) {
		return ((Lista) lista).contains(valor);
	}

	public static Lista addNew(Object lista, Object valor) {
		Lista nova = create();
		nova.addLista((Lista) lista);
		nova.add(valor);
		return nova;
	}

	public static void add(Object lista, Object valor) {
		((Lista) lista).add(valor);
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

	public static void clear(Object lista) {
		((Lista) lista).clear();
	}
}