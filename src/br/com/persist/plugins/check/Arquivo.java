package br.com.persist.plugins.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Arquivo {
	private final List<Sentenca> sentencas;

	public Arquivo() {
		this(new ArrayList<>());
	}

	public Arquivo(List<Sentenca> lista) {
		sentencas = new ArrayList<>(lista);
	}

	public List<Sentenca> getSentencas() {
		return sentencas;
	}

	public void inicializar() {
		for (Sentenca sentenca : sentencas) {
			sentenca.inicializar();
		}
	}

	public List<Object> check(Map<String, Object> map) {
		List<Object> lista = new ArrayList<>();
		for (Sentenca sentenca : getSentencas()) {
			PilhaResultParam pilha = sentenca.check(map);
			if (pilha.size() != 1) {
				throw new IllegalStateException();
			}
			lista.add(pilha.pop());
		}
		return lista;
	}
}