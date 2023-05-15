package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Sentenca;
import br.com.persist.plugins.checagem.funcao.FuncaoVaziaOuNParam;

public class Processar extends FuncaoVaziaOuNParam {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		for (Sentenca s : parametros) {
			s.executar(checagem, bloco, ctx);
		}
		return null;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "processar(funcao, funcaoN)";
	}
}