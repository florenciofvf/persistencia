package br.com.persist.plugins.checagem.colecao;

import java.util.List;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnaria;

public class SizeLista extends FuncaoUnaria {
	private static final String ERRO = "Erro sizeLista";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(ctx);
		checkObrigatorioList(op0, ERRO + " >>> op0");
		List<?> lista = (List<?>) op0;
		return Long.valueOf(lista.size());
	}
}