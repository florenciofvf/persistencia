package br.com.persist.util;

public class Coletor {
	private final String numero;
	private int total;

	public Coletor(String numero) {
		this.numero = numero;
	}

	public int getTotal() {
		return total;
	}

	public void incrementarTotal() {
		total++;
	}

	public String getNumero() {
		return numero;
	}
}