package br.com.persist.plugins.legado;

import java.io.PrintWriter;

import br.com.persist.assistencia.Util;

public class LegadoProperty extends Legado {
	@Override
	protected void gerarImpl(PrintWriter pw) {
		if (!Util.isEmpty(column)) {
			println(pw, "@Column(name = " + citar(column) + ")");
		}
		checarMetas(pw);
		printDeclaracao(pw);
	}
}