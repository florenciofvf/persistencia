package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;
import br.com.persist.assistencia.Util;

public class MetodoGet extends Objeto {
	private final Variavel variavel;

	protected MetodoGet(Variavel variavel) {
		super("MetodoGet");
		this.variavel = variavel;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		FuncaoPublica funcao = new FuncaoPublica(variavel.tipo, "get" + Util.capitalize(variavel.nome),
				new Parametros());
		funcao.addReturn(variavel.nome);
		funcao.gerar(tab, pool);
	}
}