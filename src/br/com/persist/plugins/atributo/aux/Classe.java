package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Classe extends Container {
	private final String nome;

	public Classe(String nome) {
		this.nome = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append("public class " + nome + " {").ql();
		for (Container c : lista) {
			c.gerar(1, pool);
		}
		pool.append("}");
	}
}