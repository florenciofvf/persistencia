package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Tipo extends Container {
	final String classe;
	final String nome;

	public Tipo(String classe, String nome) {
		this.classe = classe;
		this.nome = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(classe + " " + nome);
	}

	@Override
	public String toString() {
		return classe + " " + nome;
	}
}