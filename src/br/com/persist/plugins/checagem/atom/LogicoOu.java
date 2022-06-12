package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class LogicoOu extends FuncaoBinaria {
	private static final String ERRO = "Erro ou";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(ctx);
		Object op1 = param1().executar(ctx);
		checkObrigatorioBoolean(op0, ERRO + " >>> op0");
		checkObrigatorioBoolean(op1, ERRO + " >>> op1");
		Boolean pri = (Boolean) op0;
		Boolean seg = (Boolean) op1;
		return pri || seg;
	}
}