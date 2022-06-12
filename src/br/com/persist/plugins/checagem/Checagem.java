package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.checagem.atom.Sentenca;

public class Checagem {
	final Map<String, List<Sentenca>> map;

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
		List<Sentenca> sentencas = map.get(key);
		if (sentencas == null) {
			throw new ChecagemException("sentencas null.");
		}
		List<Object> resp = new ArrayList<>();
		for (Sentenca sentenca : sentencas) {
			resp.add(sentenca.executar(ctx));
		}
		return resp;
	}
}