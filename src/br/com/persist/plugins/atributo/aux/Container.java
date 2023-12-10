package br.com.persist.plugins.atributo.aux;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.StringPool;

public abstract class Container {
	protected final List<Container> lista;

	protected Container() {
		lista = new ArrayList<>();
	}

	public void add(Container c) {
		if (c != null) {
			lista.add(c);
		}
	}

	public abstract void gerar(StringPool pool);
}