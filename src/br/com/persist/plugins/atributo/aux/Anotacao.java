package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Anotacao extends Container {
	private final String nome;
	private final String valor;
	private final boolean ql;

	public Anotacao(String nome, String valor, boolean ql) {
		this.nome = nome;
		this.valor = valor;
		this.ql = ql;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("@" + nome);
		if (valor != null) {
			pool.append("(" + valor + ")");
		}
		if (ql) {
			pool.ql();
		}
	}
}