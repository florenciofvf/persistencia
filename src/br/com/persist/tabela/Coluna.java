package br.com.persist.tabela;

public class Coluna {
	private final boolean numero;
	private final boolean chave;
	private final String nome;
	private final int indice;

	public Coluna(String nome, int indice, boolean numero, boolean chave) {
		this.indice = indice;
		this.numero = numero;
		this.chave = chave;
		this.nome = nome;
	}

	public boolean isNumero() {
		return numero;
	}

	public boolean isChave() {
		return chave;
	}

	public String getNome() {
		return nome;
	}

	public int getIndice() {
		return indice;
	}

	@Override
	public String toString() {
		return nome;
	}
}