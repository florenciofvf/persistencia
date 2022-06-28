package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public class SetAtributoContexto extends FuncaoBinaria {
	private static final String ERRO = "Erro setAtributoContexto";

	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(key, ctx);
		Object op1 = param1().executar(key, ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		String pri = (String) op0;
		ctx.put(pri, op1);
		return op1;
	}
}