package br.com.persist.plugins.checagem.funcao;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class FuncaoPadrao extends FuncaoVaziaOu1Param {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		if (parametros.isEmpty()) {
			return null;
		}
		return parametros.get(0).executar(checagem, bloco, ctx);
	}
}