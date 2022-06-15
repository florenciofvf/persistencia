package br.com.persist.plugins.checagem.condicional;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoTernaria;

public class SeNao extends FuncaoTernaria {
	private static final String ERRO = "Erro senao";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(ctx);
		checkObrigatorioBoolean(op0, ERRO + " >>> op0");
		Boolean pri = (Boolean) op0;
		Object seg = param1().executar(ctx);
		Object ter = param2().executar(ctx);
		return pri ? seg : ter;
	}
}