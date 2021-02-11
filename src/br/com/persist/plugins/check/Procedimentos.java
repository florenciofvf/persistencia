package br.com.persist.plugins.check;

import java.util.HashMap;
import java.util.Map;

public class Procedimentos {
	private static final Map<String, Procedimento> map = new HashMap<>();

	static {

	}

	public static Procedimento get(String nome) {
		return map.get(nome).clonar();
	}
}