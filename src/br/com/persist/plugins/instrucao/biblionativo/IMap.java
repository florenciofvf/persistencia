package br.com.persist.plugins.instrucao.biblionativo;

import java.util.Map;

public class IMap {
	private IMap() {
	}

	@Biblio
	@SuppressWarnings("unchecked")
	public static void put(Object mapa, Object chave, Object valor) {
		((Map<Object, Object>) mapa).put(chave, valor);
	}

	@Biblio
	public static Object get(Object mapa, Object chave) {
		return ((Map<?, ?>) mapa).get(chave);
	}

	@Biblio
	public static Object size(Object mapa) {
		return ((Map<?, ?>) mapa).size();
	}
}