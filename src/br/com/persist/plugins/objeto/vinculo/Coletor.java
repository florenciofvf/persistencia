package br.com.persist.plugins.objeto.vinculo;

import java.util.Objects;

public class Coletor {
	private final String chave;
	private int total;

	public Coletor(String chave) {
		this.chave = Objects.requireNonNull(chave);
	}

	public int getTotal() {
		return total;
	}

	public void incrementarTotal() {
		total++;
	}

	public String getChave() {
		return chave;
	}
}