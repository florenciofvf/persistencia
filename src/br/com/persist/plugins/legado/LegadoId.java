package br.com.persist.plugins.legado;

import java.io.PrintWriter;

public class LegadoId extends Legado {
	@Override
	protected void gerarImpl(PrintWriter pw) {
		println(pw, "@Id");
		println(pw, "@Column(name = " + citar(column) + ", insertable = false, updatable = false)");
		checarMetas(pw);
		printDeclaracao(pw);
	}
}