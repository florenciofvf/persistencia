package br.com.persist.plugins.checagem.condicional;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public class Se extends FuncaoBinaria {
	private static final String ERRO = "Erro Se";

	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(key, ctx);
		checkObrigatorioBoolean(op0, ERRO + " >>> op0");
		Boolean pri = (Boolean) op0;
		Object seg = param1().executar(key, ctx);
		return pri ? seg : null;
	}
}