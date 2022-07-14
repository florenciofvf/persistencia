package br.com.persist.plugins.checagem.colecao;

import java.util.Map;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public class Get extends FuncaoBinaria {
	private static final String ERRO = "Erro Get";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioMap(op0, ERRO + " >>> op0");
		checkObrigatorioString(op1, ERRO + " >>> op1");
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) op0;
		return map.get((String) op1);
	}
}