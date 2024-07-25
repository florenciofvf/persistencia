package br.com.persist.plugins.instrucao.biblionativo;

public class Map {
	private Map() {
	}

	@Biblio
	@SuppressWarnings("unchecked")
	public static void put(Object mapa, Object chave, Object valor) {
		((java.util.Map<Object, Object>) mapa).put(chave, valor);
	}

	@Biblio
	public static Object get(Object mapa, Object chave) {
		return ((java.util.Map<?, ?>) mapa).get(chave);
	}

	@Biblio
	public static Object size(Object mapa) {
		return ((java.util.Map<?, ?>) mapa).size();
	}
}