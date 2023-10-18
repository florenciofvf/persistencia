package br.com.persist.plugins.propriedade;

import br.com.persist.marca.XMLUtil;

public class Bloco extends Container {
	public Bloco(String nome) {
		super(nome);
	}

	@Override
	public void adicionar(Container c) {
		if (c instanceof Param || c instanceof Propriedade) {
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