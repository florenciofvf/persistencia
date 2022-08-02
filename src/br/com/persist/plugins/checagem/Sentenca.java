package br.com.persist.plugins.checagem;

public interface Sentenca {
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException;
}