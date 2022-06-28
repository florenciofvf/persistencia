package br.com.persist.plugins.checagem.colecao;

import java.util.Collection;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnaria;

public class TamanhoColecao extends FuncaoUnaria {
	private static final String ERRO = "Erro TamanhoColecao";

	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(key, ctx);
		checkObrigatorioCollection(op0, ERRO + " >>> op0");
		Collection<?> colecao = (Collection<?>) op0;
		return colecao.size();
	}
}