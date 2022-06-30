package br.com.persist.plugins.checagem;

public class FuncaoPadrao extends FuncaoVaziaOu1Param {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		if (parametros.isEmpty()) {
			return null;
		}
		return parametros.get(0).executar(checagem, bloco, ctx);
	}
}