package br.com.persist.plugins.atributo.aux;

import br.com.persist.assistencia.StringPool;

public class Return extends Container {
	final String prefixo;
	final String valor;

	public Return(String prefixo, String valor) {
		this.prefixo = prefixo;
		this.valor = valor;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		pool.tab(tab).append("return " + prefixo + valor).append(";").ql();
	}
}