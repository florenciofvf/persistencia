package br.com.persist.plugins.instrucao.biblionativo;

public class IList {
	private IList() {
	}

	@Biblio(7)
	public static Lista create() {
		return new Lista();
	}

	@Biblio(1)
	public static Object notContains(Object lista, Object valor) {
		return ((Lista) lista).notContains(valor);
	}

	@Biblio(5)
	public static Object contains(Object lista, Object valor) {
		return ((Lista) lista).contains(valor);
	}

	@Biblio(2)
	public static Lista addNew(Object lista, Object valor) throws IllegalAccessException {
		Lista nova = create();
		nova.addLista((Lista) lista);
		nova.add(valor);
		return nova;
	}

	@Biblio(3)
	public static void add(Object lista, Object valor) {
		((Lista) lista).add(valor);
	}

	@Biblio(6)
	public static Object size(Object lista) {
		return ((Lista) lista).size();
	}

	@Biblio(9)
	public static Object empty(Object lista) {
		return ((Lista) lista).empty();
	}

	@Biblio(8)
	public static Object head(Object lista) throws IllegalAccessException {
		return ((Lista) lista).head();
	}

	@Biblio(10)
	public static Object tail(Object lista) throws IllegalAccessException {
		return ((Lista) lista).tail();
	}

	@Biblio(4)
	public static void clear(Object lista) {
		((Lista) lista).clear();
	}
}