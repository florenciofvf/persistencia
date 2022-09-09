package br.com.persist.data;

public class Nulo implements Tipo {
	public static final String CONTEUDO = "null";

	@Override
	public String toString() {
		return CONTEUDO;
	}
}