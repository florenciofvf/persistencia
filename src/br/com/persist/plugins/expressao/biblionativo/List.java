package br.com.persist.plugins.expressao.biblionativo;

public class List {
	private List() {
	}

	@Biblio(1)
	public static Lista create() {
		return new Lista();
	}

	@Biblio(2)
	public static Lista createComLista(Object lista, Object valor) throws IllegalAccessException {
		Lista nova = create();
		nova.concat((Lista) lista);
		nova.add(valor);
		return nova;
	}

	@Biblio(3)
	public static void clear(Object lista) {
		((Lista) lista).clear();
	}

	@Biblio(4)
	public static Object head(Object lista) throws IllegalAccessException {
		return ((Lista) lista).head();
	}

	@Biblio(5)
	public static Object tail(Object lista) throws IllegalAccessException {
		return ((Lista) lista).tail();
	}

	@Biblio(6)
	public static void add(Object lista, Object valor) {
		((Lista) lista).add(valor);
	}

	@Biblio(7)
	public static Object contains(Object lista, Object valor) {
		return ((Lista) lista).contains(valor);
	}

	@Biblio(8)
	public static Object notContains(Object lista, Object valor) {
		return ((Lista) lista).notContains(valor);
	}

	@Biblio(9)
	public static Object size(Object lista) {
		return ((Lista) lista).size();
	}

	@Biblio(10)
	public static Object empty(Object lista) {
		return ((Lista) lista).empty();
	}
}