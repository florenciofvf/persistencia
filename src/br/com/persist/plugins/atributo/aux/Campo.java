package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Campo extends Container {
	private final Anotacao anotacao;
	private final Tipo tipo;

	public Campo(Anotacao anotacao, Tipo tipo) {
		this.anotacao = anotacao;
		this.tipo = tipo;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		if (anotacao != null) {
			anotacao.gerar(tab, pool);
			pool.ql();
		}
		pool.tab(tab).append("private ");
		tipo.gerar(0, pool);
		pool.append(";").ql();
	}
}