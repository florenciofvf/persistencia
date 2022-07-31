package br.com.persist.plugins.checagem.logico;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinariaInfixa;

public class E extends FuncaoBinariaInfixa {
	private static final String ERRO = "Erro E";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioBoolean(op0, ERRO + " >>> op0");
		Boolean pri = (Boolean) op0;
		if (!pri) {
			return Boolean.FALSE;
		}
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioBoolean(op1, ERRO + " >>> op1");
		return ((Boolean) op1).booleanValue();
	}
}