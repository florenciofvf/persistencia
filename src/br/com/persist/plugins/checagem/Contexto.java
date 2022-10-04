package br.com.persist.plugins.checagem;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Contexto {
	private final Map<String, Object> map = new HashMap<>();

	public Contexto(Map<String, Object> map) {
		put(map);
	}

	public Contexto() {
	}

	public Map<String, Object> getMap() {
		return new HashMap<>(map);
	}

	public Object get(String chave) {
		return map.get(chave.toLowerCase());
	}

	public void put(String chave, Object valor) {
		map.put(chave.toLowerCase(), valor);
	}

	public void put(Map<String, Object> map) {
		if (map != null) {
			for (Entry<String, Object> entry : map.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	public static Contexto criar(Object object) {
		Contexto ctx = new Contexto();
		if (object instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) object;
			for (Entry<?, ?> entry : map.entrySet()) {
				ctx.put(entry.getKey().toString(), entry.getValue());
			}
		}
		return ctx;
	}
}