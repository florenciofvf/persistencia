package br.com.persist.plugins.checagem.logico;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public class Oux extends FuncaoBinaria {
	private static final String ERRO = "Erro Oux";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioBoolean(op0, ERRO + " >>> op0");
		checkObrigatorioBoolean(op1, ERRO + " >>> op1");
		Boolean pri = (Boolean) op0;
		Boolean seg = (Boolean) op1;
		return pri ^ seg;
	}
}