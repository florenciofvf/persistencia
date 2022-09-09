package br.com.persist.data;

public class Nulo extends Tipo {
	public static final String CONTEUDO = "null";

	@Override
	public String toString() {
		return CONTEUDO;
	}
}