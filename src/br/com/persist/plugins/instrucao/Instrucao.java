package br.com.persist.plugins.instrucao;

import br.com.persist.assistencia.Util;

public abstract class Instrucao {
	protected final Metodo metodo;
	protected String parametro;

	public Instrucao(Metodo metodo) {
		this.metodo = metodo;
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