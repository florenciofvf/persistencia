package br.com.persist.plugins.checagem;

public abstract class Sentenca {
	protected Sentenca pai;

	public abstract Object executar(String key, Contexto ctx) throws ChecagemException;

	public Sentenca getPai() {
		return pai;
	}
}