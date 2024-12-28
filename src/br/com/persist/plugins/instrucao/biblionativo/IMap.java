package br.com.persist.plugins.instrucao.biblionativo;

import java.util.HashMap;
import java.util.Map;

public class IMap {
	private IMap() {
	}

	@Biblio(5)
	public static Map<Object, Object> create() {
		return new HashMap<>();
	}

	@Biblio(3)
	@SuppressWarnings("unchecked")
	public static void put(Object mapa, Object chave, Object valor) {
		((Map<Object, Object>) mapa).put(chave, valor);
	}

	@Biblio(1)
	public static Object getOr(Object mapa, Object chave, Object padrao) {
		Object resp = ((Map<?, ?>) mapa).get(chave);
		return resp == null ? padrao : resp;
	}

	@Biblio(2)
	public static Object get(Object mapa, Object chave) {
		return ((Map<?, ?>) mapa).get(chave);
	}

	@Biblio(4)
	public static Object size(Object mapa) {
		return ((Map<?, ?>) mapa).size();
	}
}