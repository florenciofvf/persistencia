package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoVazia;

public class Null extends FuncaoVazia {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		return null;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "null()";
	}
}