package br.com.persist.plugins.instrucao;

import java.util.Objects;

import br.com.persist.assistencia.Util;

public abstract class Instrucao {
	protected final Metodo metodo;
	protected final String nome;
	protected String parametro;

	public Instrucao(Metodo metodo, String nome) {
		this.nome = Objects.requireNonNull(nome);
		this.metodo = metodo;
	}

	public String getNome() {
		return nome;
	}

	public abstract Instrucao clonar(Metodo metodo);

	public abstract void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando,
			CacheBiblioteca cacheBiblioteca);

	public void setParam(String string) {
		if (!Util.estaVazio(string)) {
			parametro = string;
		}
	}
}