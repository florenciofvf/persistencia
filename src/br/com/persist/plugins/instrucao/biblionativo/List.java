package br.com.persist.plugins.instrucao.biblionativo;

public class List {
	private List() {
	}

	@Biblio
	public static Lista create() {
		return new Lista();
	}

	@Biblio
	public static Object notContains(Object lista, Object valor) {
		return ((Lista) lista).notContains(valor);
	}

	@Biblio
	public static Object contains(Object lista, Object valor) {
		return ((Lista) lista).contains(valor);
	}

	@Biblio
	public static Lista addNew(Object lista, Object valor) throws IllegalAccessException {
		Lista nova = create();
		nova.addLista((Lista) lista);
		nova.add(valor);
		return nova;
	}

	@Biblio
	public static void add(Object lista, Object valor) {
		((Lista) lista).add(valor);
	}

	@Biblio
	public static Object size(Object lista) {
		return ((Lista) lista).size();
	}

	@Biblio
	public static Object empty(Object lista) {
		return ((Lista) lista).empty();
	}

	@Biblio
	public static Object head(Object lista) throws IllegalAccessException {
		return ((Lista) lista).head();
	}

	@Biblio
	public static Object tail(Object lista) throws IllegalAccessException {
		return ((Lista) lista).tail();
	}

	@Biblio
	public static void clear(Object lista) {
		((Lista) lista).clear();
	}
}