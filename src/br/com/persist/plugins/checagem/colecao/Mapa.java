package br.com.persist.plugins.checagem.colecao;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoVaziaOuNParam;

public class Mapa extends FuncaoVaziaOuNParam {
	private static final String ERRO = "Erro Mapa";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Map<String, Object> resposta = new HashMap<>();
		for (int i = 0; i < parametros.size(); i += 2) {
			Object nomeParametro = parametros.get(i).executar(checagem, bloco, ctx);
			checkObrigatorioString(nomeParametro, ERRO + " >>> op" + i);
			int indiceValor = i + 1;
			if (indiceValor >= parametros.size()) {
				throw new ChecagemException(getClass(), "Parametro sem valor >>> " + nomeParametro);
			}
			Object valorParametro = parametros.get(indiceValor).executar(checagem, bloco, ctx);
			resposta.put(nomeParametro.toString(), valorParametro);
		}
		return resposta;
	}
}