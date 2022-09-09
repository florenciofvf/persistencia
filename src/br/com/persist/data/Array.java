package br.com.persist.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Array implements Tipo {
	private final List<Tipo> elementos;

	public Array() {
		elementos = new ArrayList<>();
	}

	public List<Tipo> getElementos() {
		return elementos;
	}

	public void addElemento(Tipo valor) {
		elementos.add(Objects.requireNonNull(valor));
	}

	public Tipo getElemento(int indice) {
		return elementos.get(indice);
	}
}