package br.com.persist.plugins.biblio;

import java.io.File;

public class Biblio {
	private String nome;
	private File file;

	public Biblio() {
		this("");
	}

	public Biblio(String nome) {
		setNome(nome);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		file = new File(nome);
		this.nome = nome;
	}

	public File getFile() {
		return file;
	}
}