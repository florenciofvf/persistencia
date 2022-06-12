package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;

public class ComparacaoMenorIgual extends FuncaoBinaria {
	@Override
	public Object executar(Contexto ctx) {
		Object pri = param0().executar(ctx);
		Object seg = param1().executar(ctx);
		if (pri instanceof Long) {
			if (seg instanceof Long) {
				return ((Long) pri) <= ((Long) seg);
			} else if (seg instanceof Double) {
				return ((Long) pri) <= ((Double) seg);
			}
		} else if (pri instanceof Double) {
			if (seg instanceof Long) {
				return ((Double) pri) <= ((Long) seg);
			} else if (seg instanceof Double) {
				return ((Double) pri) <= ((Double) seg);
			}
		}
		throw new IllegalStateException();
	}
}