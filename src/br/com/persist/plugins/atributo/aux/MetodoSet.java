package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;
import br.com.persist.assistencia.Util;

public class MetodoSet extends Container {
	private final Tipo tipo;

	public MetodoSet(Tipo tipo) {
		this.tipo = tipo;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		Parametros params = new Parametros();
		params.add(new Parametro(tipo));
		Funcao funcao = new Funcao("public", "void", "set" + Util.capitalize(tipo.nome), params);
		funcao.add(new Atribuir("this.", tipo.nome, tipo.nome));
		funcao.gerar(tab, pool);
	}
}