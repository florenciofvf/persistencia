package br.com.persist.plugins.checagem.colecao;

import java.util.Map;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnariaOuNParam;

public class Put extends FuncaoUnariaOuNParam {
	private static final String ERRO = "Erro Put";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioMap(op0, ERRO + " >>> op0");
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) op0;
		for (int i = 1; i < parametros.size(); i += 2) {
			Object nomeParametro = parametros.get(i).executar(checagem, bloco, ctx);
			checkObrigatorioString(nomeParametro, ERRO + " >>> op" + i);
			int indiceValor = i + 1;
			if (indiceValor >= parametros.size()) {
				throw new ChecagemException(getClass(), "Parametro sem valor >>> " + nomeParametro);
			}
			Object valorParametro = parametros.get(indiceValor).executar(checagem, bloco, ctx);
			map.put(nomeParametro.toString(), valorParametro);
		}
		return op0;
	}
}