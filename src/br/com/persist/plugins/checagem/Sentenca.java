package br.com.persist.plugins.checagem;

public abstract class Sentenca {
	protected Sentenca pai;

	public abstract Object executar(Contexto ctx) throws ChecagemException;

	public Sentenca getPai() {
		return pai;
	}
}