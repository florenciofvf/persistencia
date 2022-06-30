package br.com.persist.plugins.checagem;

public abstract class Sentenca {
	protected Sentenca pai;

	public abstract Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException;

	public Sentenca getPai() {
		return pai;
	}
}