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

	@Biblio(6)
	public static Map<Object, Object> createOf(Object lista) throws IllegalAccessException {
		Map<Object, Object> mapa = create();
		if (lista instanceof Lista) {
			Lista list = (Lista) lista;
			long size = list.size().longValue();
			for (long i = 0; i < size; i += 2) {
				Object chave = list.get(i);
				Object valor = "";
				if (i + 1 < size) {
					valor = list.get(i + 1);
				}
				mapa.put(chave, valor);
			}
		}
		return mapa;
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
		return getOr(mapa, chave, "");
	}

	@Biblio(4)
	public static Object size(Object mapa) {
		return ((Map<?, ?>) mapa).size();
	}
}