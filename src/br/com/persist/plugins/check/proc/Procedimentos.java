package br.com.persist.plugins.check.proc;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.plugins.check.Procedimento;

public final class Procedimentos {
	private static final Map<String, Procedimento> map = new HashMap<>();

	private Procedimentos() {
	}

	static {
		map.put("parseInt", new ParseInt());
		map.put("somar", new Somar());
	}

	public static Procedimento get(String nome) {
		return map.get(nome).clonar();
	}

	public static void clonarParametros(Procedimento origem, Procedimento destino) {
		for (Object obj : origem.getParametros()) {
			destino.addParam(obj);
		}
	}
}