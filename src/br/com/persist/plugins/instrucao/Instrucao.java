package br.com.persist.plugins.instrucao;

public abstract class Instrucao {
	protected final Metodo metodo;

	public Instrucao(Metodo metodo) {
		this.metodo = metodo;
	}

	public abstract Instrucao clonar(Metodo metodo);

	public abstract void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando,
			CacheBiblioteca cacheBiblioteca);
}