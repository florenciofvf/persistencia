package br.com.persist.plugins.instrucao;

import java.util.ArrayList;
import java.util.List;

public class PilhaMetodo {
	private final List<Metodo> metodos;

	public PilhaMetodo() {
		metodos = new ArrayList<>();
	}

	public Metodo ref() {
		return metodos.get(metodos.size() - 1);
	}

	public void add(Metodo metodo) {
		if (metodo != null) {
			metodos.add(metodo);
		}
	}

	public Metodo remove() {
		return metodos.remove(metodos.size() - 1);
	}
}