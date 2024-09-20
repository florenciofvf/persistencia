package br.com.persist.plugins.instrucao.biblionativo;

import java.util.HashMap;
import java.util.Map;

public class IMap {
	private IMap() {
	}

	@Biblio
	public static Map<Object, Object> create() {
		return new HashMap<>();
	}

	@Biblio
	@SuppressWarnings("unchecked")
	public static void put(Object mapa, Object chave, Object valor) {
		((Map<Object, Object>) mapa).put(chave, valor);
	}

	@Biblio
	public static Object getOr(Object mapa, Object chave, Object padrao) {
		Object resp = ((Map<?, ?>) mapa).get(chave);
		return resp == null ? padrao : resp;
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