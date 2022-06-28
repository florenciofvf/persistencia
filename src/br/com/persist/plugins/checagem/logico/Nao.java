package br.com.persist.plugins.checagem.logico;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnaria;

public class Nao extends FuncaoUnaria {
	private static final String ERRO = "Erro Nao";

	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(key, ctx);
		checkObrigatorioBoolean(op0, ERRO + " >>> op0");
		Boolean pri = (Boolean) op0;
		return !pri;
	}
}