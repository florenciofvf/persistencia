package br.com.persist.plugins.atributo;

public class Atributo {
	private String nome;
	private boolean ignorar;

	public Atributo() {
		this(null);
	}

	public Atributo(String nome) {
		this(nome, false);
	}

	public Atributo(String nome, boolean ignorar) {
		this.nome = nome;
		this.ignorar = ignorar;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isIgnorar() {
		return ignorar;
	}

	public void setIgnorar(boolean ignorar) {
		this.ignorar = ignorar;
	}

	@Override
	public String toString() {
		return nome + " ignorar: " + ignorar;
	}
}