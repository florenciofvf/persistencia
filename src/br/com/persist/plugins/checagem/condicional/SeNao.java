package br.com.persist.plugins.checagem.condicional;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoTernaria;

public class SeNao extends FuncaoTernaria {
	private static final String ERRO = "Erro SeNao";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioBoolean(op0, ERRO + " >>> op0");
		Boolean pri = (Boolean) op0;
		return pri ? param1().executar(checagem, bloco, ctx) : param2().executar(checagem, bloco, ctx);
	}
}