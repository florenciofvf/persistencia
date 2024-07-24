package br.com.persist.plugins.instrucao.biblionativo;

public class Map {
	private Map() {
	}

	@SuppressWarnings("unchecked")
	public static void put(Object mapa, Object chave, Object valor) {
		((java.util.Map<Object, Object>) mapa).put(chave, valor);
	}

	public static Object get(Object mapa, Object chave) {
		return ((java.util.Map<?, ?>) mapa).get(chave);
	}

	public static Object size(Object mapa) {
		return ((java.util.Map<?, ?>) mapa).size();
	}
}