package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Sentenca;
import br.com.persist.plugins.checagem.funcao.FuncaoVaziaOuNParam;

public class Concatenar extends FuncaoVaziaOuNParam {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		StringBuilder resposta = new StringBuilder();
		for (Sentenca s : parametros) {
			Object obj = s.executar(checagem, bloco, ctx);
			resposta.append(obj);
		}
		return resposta;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "concat(Objeto, ObjetoN) : Texto";
	}
}