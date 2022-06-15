package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public class ComparacaoMaior extends FuncaoBinaria {
	private static final String ERRO = "Erro maior";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(ctx);
		Object seg = param1().executar(ctx);
		if (pri instanceof Long) {
			if (seg instanceof Long) {
				return ((Long) pri) > ((Long) seg);
			} else if (seg instanceof Double) {
				return ((Long) pri) > ((Double) seg);
			}
		} else if (pri instanceof Double) {
			if (seg instanceof Long) {
				return ((Double) pri) > ((Long) seg);
			} else if (seg instanceof Double) {
				return ((Double) pri) > ((Double) seg);
			}
		}
		throw new ChecagemException(ERRO);
	}
}