package br.com.persist.plugins.checagem.colecao;

import java.util.HashSet;
import java.util.Set;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoVaziaOuNParam;
import br.com.persist.plugins.checagem.Sentenca;

public class CriarSet extends FuncaoVaziaOuNParam {

	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		Set<Object> resposta = new HashSet<>();
		for (Sentenca s : parametros) {
			resposta.add(s.executar(key, ctx));
		}
		return resposta;
	}
}