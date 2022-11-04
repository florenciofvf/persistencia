package br.com.persist.plugins.checagem.colecao;

import java.util.HashSet;
import java.util.Set;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Sentenca;
import br.com.persist.plugins.checagem.funcao.FuncaoVaziaOuNParam;

public class CriarSet extends FuncaoVaziaOuNParam {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Set<Object> resposta = new HashSet<>();
		for (Sentenca s : parametros) {
			resposta.add(s.executar(checagem, bloco, ctx));
		}
		return resposta;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "criarSet(objeto, objetoN) : Set";
	}
}