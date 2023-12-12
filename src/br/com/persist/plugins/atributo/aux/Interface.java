package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Interface extends ContainerString {
	public Interface(String nome) {
		super(nome);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.append("public interface " + string + " {").ql();
		super.gerar(1, pool);
		pool.append("}");
	}
}