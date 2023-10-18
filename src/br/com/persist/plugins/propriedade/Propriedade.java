package br.com.persist.plugins.propriedade;

import br.com.persist.marca.XMLUtil;

public class Propriedade extends Container {
	public Propriedade(String nome, String valor) {
		super(nome);
		setValor(valor);
	}

	@Override
	public void adicionar(Container c) {
		throw new IllegalStateException();
	}

	@Override
	public void salvar(Container pai, XMLUtil util) {
		// TODO Auto-generated method stub
	}
}