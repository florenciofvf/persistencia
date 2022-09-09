package br.com.persist.data;

import java.util.ArrayList;
import java.util.List;

public class Array extends Tipo {
	private final List<Tipo> elementos;

	public Array() {
		elementos = new ArrayList<>();
	}

	public List<Tipo> getElementos() {
		return elementos;
	}

	public void addElemento(Tipo tipo) {
		tipo.pai = this;
		elementos.add(tipo);
	}

	public Tipo getElemento(int indice) {
		return elementos.get(indice);
	}

	public void preElemento() {
	}

	@Override
	public String toString() {
		return elementos.toString();
	}
}