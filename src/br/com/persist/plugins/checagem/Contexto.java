package br.com.persist.plugins.checagem;

import java.util.HashMap;
import java.util.Map;

public class Contexto {
	private final Map<String, Object> map;

	public Contexto(Map<String, Object> map) {
		this.map = map != null ? map : new HashMap<>();
	}

	public Contexto() {
		this(null);
	}

	public Object get(String chave) {
		return map.get(chave);
	}

	public void put(String chave, String valor) {
		map.put(chave, valor);
	}
}