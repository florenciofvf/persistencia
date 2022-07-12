package br.com.persist.plugins.checagem.colecao;

import br.com.persist.assistencia.Lista;
import br.com.persist.assistencia.ListaException;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnaria;

public class Cabeca extends FuncaoUnaria {
	private static final String ERRO = "Erro Cabeca";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioLista(op0, ERRO + " >>> op0");
		try {
			Lista<?> lista = (Lista<?>) op0;
			return lista.getCabeca();
		} catch (ListaException ex) {
			throw new ChecagemException(getClass(), ERRO + " >>> " + ex.getMessage());
		}
	}
}