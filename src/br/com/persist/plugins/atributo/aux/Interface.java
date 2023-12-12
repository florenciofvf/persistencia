package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Interface extends Container {
	private final String nome;

	public Interface(String nome) {
		this.nome = nome;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append("public interface " + nome + " {").ql();
		super.gerar(1, pool);
		pool.append("}");
	}
}