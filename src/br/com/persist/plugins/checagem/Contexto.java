package br.com.persist.plugins.checagem;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Contexto {
	private final Map<String, Object> map = new HashMap<>();
	private Contexto parent;

	public Contexto(Map<String, Object> map) {
		put(map);
	}

	public Contexto() {
	}

	public Map<String, Object> getMap() {
		return new HashMap<>(map);
	}

	public Object get(String chave) {
		Contexto ctx = this;
		while (ctx != null) {
			Object obj = ctx.map.get(chave.toLowerCase());
			if (obj != null) {
				return obj;
			}
			ctx = ctx.parent;
		}
		return null;
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

	public Contexto criar(Object object) {
		Contexto ctx = new Contexto();
		ctx.parent = this;
		if (object instanceof Map<?, ?>) {
			Map<?, ?> mapa = (Map<?, ?>) object;
			for (Entry<?, ?> entry : mapa.entrySet()) {
				ctx.put(entry.getKey().toString(), entry.getValue());
			}
		}
		return ctx;
	}
}