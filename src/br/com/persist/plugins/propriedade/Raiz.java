package br.com.persist.plugins.propriedade;

import br.com.persist.marca.XMLUtil;

public class Raiz extends Container {
	public Raiz() {
		super(null);
	}

	@Override
	public void adicionar(Container c) {
		if (c instanceof Config || c instanceof Bloco) {
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