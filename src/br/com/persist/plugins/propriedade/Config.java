package br.com.persist.plugins.propriedade;

import br.com.persist.marca.XMLUtil;

public class Config extends Container {
	public Config(String nome) {
		super(nome);
	}

	@Override
	public void adicionar(Container c) {
		if (c instanceof Atributo) {
			super.adicionar(c);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public void salvar(Container pai, XMLUtil util) {
		// TODO Auto-generated method stub
	}
}