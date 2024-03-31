package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;
import br.com.persist.assistencia.Util;

public class MetodoSet extends Container {
	private final Variavel variavel;

	protected MetodoSet(Variavel variavel) {
		super("MetodoSet");
		this.variavel = variavel;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		Parametros params = new Parametros();
		params.add(variavel);
		FuncaoPublica funcao = new FuncaoPublica("void", "set" + Util.capitalize(variavel.nome), params);
		funcao.add(new Atribuir("this." + variavel.nome, variavel.nome));
		funcao.gerar(tab, pool);
	}
}