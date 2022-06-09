package br.com.persist.plugins.checagem;

public class OpLogicoNao extends Controle {

	@Override
	public Object executar(Contexto ctx) {
		Boolean arq = (Boolean) param0().executar(ctx);
		return !arq;
	}
}