package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public class SetAtributoContexto extends FuncaoBinaria {
	private static final String ERRO = "Erro setAtributoContexto";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		ctx.put((String) op0, op1);
		return op1;
	}
}