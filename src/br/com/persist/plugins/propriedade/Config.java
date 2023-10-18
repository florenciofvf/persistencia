package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;

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

	public List<Atributo> getAtributos() {
		List<Atributo> resp = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Atributo) {
				resp.add((Atributo) c);
			}
		}
		return resp;
	}
}