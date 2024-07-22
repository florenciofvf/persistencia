package br.com.persist.plugins.instrucao.biblionativo;

import java.util.Map;

public class Mapa {
	private Mapa() {
	}

	public static Object get(Object mapa, Object chave) {
		return ((Map<?, ?>) mapa).get(chave);
	}
}