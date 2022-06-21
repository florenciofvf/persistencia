package br.com.persist.plugins.checagem.colecao;

import java.util.Collection;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public class ContemTodos extends FuncaoBinaria {
	private static final String ERRO = "Erro ContemTodos";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(ctx);
		Object op1 = param1().executar(ctx);
		checkObrigatorioCollection(op0, ERRO + " >>> op0");
		checkObrigatorioCollection(op1, ERRO + " >>> op1");
		Collection<?> colecao = (Collection<?>) op0;
		Collection<?> colecao2 = (Collection<?>) op1;
		return colecao.containsAll(colecao2);
	}
}