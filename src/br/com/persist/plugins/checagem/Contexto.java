package br.com.persist.plugins.checagem;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

	public void put(String chave, Object valor) {
		map.put(chave, valor);
	}

	public static Contexto criar(Object object) {
		Contexto ctx = new Contexto();
		if (object instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) object;
			for (Entry<?, ?> entry : map.entrySet()) {
				ctx.map.put(entry.getKey().toString(), entry.getValue());
			}
		}
		return ctx;
	}
}