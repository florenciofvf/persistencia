package br.com.persist.plugins.checagem.colecao;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoVaziaOuNParam;
import br.com.persist.plugins.checagem.Sentenca;

public class CriarLista extends FuncaoVaziaOuNParam {

	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		List<Object> resposta = new ArrayList<>();
		for (Sentenca s : parametros) {
			resposta.add(s.executar(key, ctx));
		}
		return resposta;
	}
}