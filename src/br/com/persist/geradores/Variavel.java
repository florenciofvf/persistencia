package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class Variavel extends Objeto {
	final String tipo;
	final String nome;

	public Variavel(String tipo, String nome) {
		super("Variavel");
		this.tipo = tipo;
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append(tipo + " " + nome);
	}
}