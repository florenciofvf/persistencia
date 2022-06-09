package br.com.persist.plugins.checagem;

import java.util.HashMap;
import java.util.Map;

public class Contexto {
	private final Map<String, String> map;

	public Contexto(Map<String, String> map) {
		this.map = map != null ? map : new HashMap<>();
	}

	public Contexto() {
		this(null);
	}

	public String get(String key) {
		return map.get(key);
	}
}