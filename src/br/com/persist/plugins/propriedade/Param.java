package br.com.persist.plugins.propriedade;

public class Param extends Container {
	public Param(String nome, String valor) {
		super(nome);
		setValor(valor);
	}

	@Override
	public void adicionar(Container c) {
		throw new IllegalStateException();
	}
}