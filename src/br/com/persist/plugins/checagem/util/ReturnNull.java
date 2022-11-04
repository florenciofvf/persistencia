package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoUnaria;

public class ReturnNull extends FuncaoUnaria {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		param0().executar(checagem, bloco, ctx);
		return null;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "returnNull(funcao)";
	}
}