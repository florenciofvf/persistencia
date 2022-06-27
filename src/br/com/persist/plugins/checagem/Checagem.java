package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checagem {
	final Map<String, List<Set>> map;

	public Checagem() {
		map = new HashMap<>();
	}

	public List<Object> processar(String key, Contexto ctx) throws ChecagemException {
		if (key == null) {
			throw new ChecagemException("key null.");
		}
		if (ctx == null) {
			throw new ChecagemException("ctx null.");
		}
		List<Set> sentencas = map.get(key);
		if (sentencas == null) {
			throw new ChecagemException("sentencas null.");
		}
		List<Object> resp = new ArrayList<>();
		for (Set set : sentencas) {
			if (set.getSentenca() != null) {
				resp.add(set.getSentenca().executar(ctx));
			}
		}
		return resp;
	}

	public Object processar(String key, Contexto ctx, String id) throws ChecagemException {
		if (id == null || id.trim().length() == 0) {
			return null;
		}
		if (key == null) {
			throw new ChecagemException("key null.");
		}
		if (ctx == null) {
			throw new ChecagemException("ctx null.");
		}
		List<Set> sentencas = map.get(key);
		if (sentencas == null) {
			throw new ChecagemException("sentencas null.");
		}
		for (Set set : sentencas) {
			if (set.getSentenca() != null && id.equalsIgnoreCase(set.getId())) {
				return set.getSentenca().executar(ctx);
			}
		}
		return null;
	}
}