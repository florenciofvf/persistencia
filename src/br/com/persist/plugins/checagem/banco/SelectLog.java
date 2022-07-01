package br.com.persist.plugins.checagem.banco;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinariaOuNParam;

public class SelectLog extends FuncaoBinariaOuNParam {
	private static final String ERRO = "Erro SelectLog";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		String instrucao = (String) op0;
		for (int i = 1; i < parametros.size(); i += 2) {
			Object nomeParametro = parametros.get(i).executar(checagem, bloco, ctx);
			checkObrigatorioString(nomeParametro, ERRO + " >>> op" + i);
			int indiceValor = i + 1;
			if (indiceValor >= parametros.size()) {
				throw new ChecagemException("Parametro sem valor >>> " + nomeParametro);
			}
			Object valorParametro = parametros.get(indiceValor).executar(checagem, bloco, ctx);
			instrucao = Select.substituirParametro(instrucao, (String) nomeParametro, valorParametro);
		}
		return instrucao;
	}
}