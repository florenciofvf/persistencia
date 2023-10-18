package br.com.persist.plugins.propriedade;

public class Atributo extends Container {
	public Atributo(String nome, String valor) {
		super(nome);
		setValor(valor);
	}

	@Override
	public void adicionar(Container c) {
		throw new IllegalStateException();
	}
}