package br.com.persist.plugins.checagem;

public class FuncaoPadrao extends FuncaoVaziaOuNParam {
	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		if (parametros.isEmpty()) {
			return null;
		}
		return parametros.get(0).executar(ctx);
	}
}