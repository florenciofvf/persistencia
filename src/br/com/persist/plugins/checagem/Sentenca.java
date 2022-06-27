package br.com.persist.plugins.checagem;

public abstract class Sentenca {
	protected Sentenca pai;
	private String key;

	public abstract Object executar(Contexto ctx) throws ChecagemException;

	public Sentenca getPai() {
		return pai;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}