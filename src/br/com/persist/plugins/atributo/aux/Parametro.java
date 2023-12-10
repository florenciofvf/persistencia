package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Parametro extends Container {
	private final Anotacao anotacao;
	private final Tipo tipo;

	public Parametro(Anotacao anotacao, Tipo tipo) {
		this.anotacao = anotacao;
		this.tipo = tipo;
	}

	@Override
	public void add(Container c) {
		//
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		if (anotacao != null) {
			anotacao.gerar(tab, pool);
		}
		tipo.gerar(tab, pool);
	}
}