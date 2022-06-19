package br.com.persist.plugins.checagem.colecao;

import java.util.Collection;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public class Contem extends FuncaoBinaria {
	private static final String ERRO = "Erro Contem";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(ctx);
		Object op1 = param1().executar(ctx);
		checkObrigatorioCollection(op0, ERRO + " >>> op0");
		Collection<?> colecao = (Collection<?>) op0;
		return colecao.contains(op1);
	}
}