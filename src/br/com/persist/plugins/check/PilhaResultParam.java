package br.com.persist.plugins.check;

import java.util.ArrayList;
import java.util.List;

public class PilhaResultParam {
	private final List<Object> lista;

	public PilhaResultParam() {
		lista = new ArrayList<>();
	}

	public int size() {
		return lista.size();
	}

	public Object pop() {
		return lista.remove(lista.size() - 1);
	}

	public void push(Object obj) {
		lista.add(obj);
	}

	@Override
	public String toString() {
		return lista.toString();
	}
}