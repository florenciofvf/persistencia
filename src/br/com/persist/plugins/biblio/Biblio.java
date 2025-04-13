package br.com.persist.plugins.biblio;

public class Biblio {
	private String nome;

	public Biblio() {
		this("");
	}

	public Biblio(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}