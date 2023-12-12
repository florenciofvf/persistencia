package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Var extends Container {
	final String nome;

	public Var(String nome) {
		this.nome = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(nome);
	}

	@Override
	public String toString() {
		return nome;
	}
}