package br.com.persist.plugins.instrucao.pro;

import java.util.HashMap;
import java.util.Map;

public class Instrucoes {
	static final Map<String, Instrucao> cache = new HashMap<>();

	private Instrucoes() {
	}

	static void add(Instrucao instrucao) {
		if (instrucao != null) {
			cache.put(instrucao.getNome(), instrucao);
		}
	}

	static Instrucao get(String nome) {
		return cache.get(nome);
	}
}