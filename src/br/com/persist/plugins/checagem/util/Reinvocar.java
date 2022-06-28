package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.ChecagemUtil;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnaria;

public class Reinvocar extends FuncaoUnaria {
	private static final String ERRO = "Erro Reinvocar";

	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(key, ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		return ChecagemUtil.processar(key, ctx, (String) op0);
	}
}