package br.com.persist.plugins.objeto.auto;

public class BuscaAutoColetor {
	private final String chave;
	private int total;

	public BuscaAutoColetor(String chave) {
		this.chave = chave;
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