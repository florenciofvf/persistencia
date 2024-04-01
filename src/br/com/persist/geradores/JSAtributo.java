package br.com.persist.geradores;

import br.com.persist.assistencia.StringPool;

public class JSAtributo extends ObjetoString {
	private final boolean separar;
	private final String valor;

	public JSAtributo(boolean separar, String nome, String valor) {
		super("JSAtributo", nome);
		this.separar = separar;
		this.valor = valor;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append(string + ": " + valor);
		if (separar) {
			pool.append(",");
		}
		pool.ql();
	}
}