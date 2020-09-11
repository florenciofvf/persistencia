package br.com.persist.plugins.objeto.auto;

public class BuscaAutoColetor {
	private final String numero;
	private int total;

	public BuscaAutoColetor(String numero) {
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