package br.com.persist.plugins.persistencia;

import java.util.ArrayList;
import java.util.List;

public class Registro {
	private final List<ChaveValor> lista;

	public Registro() {
		lista = new ArrayList<>();
	}

	public void add(ChaveValor cv) {
		if (cv != null) {
			lista.add(cv);
		}
	}

	public int getTotal() {
		return lista.size();
	}

	public ChaveValor get(int i) {
		return lista.get(i);
	}
}