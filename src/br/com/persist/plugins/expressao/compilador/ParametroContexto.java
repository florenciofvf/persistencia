package br.com.persist.plugins.expressao.compilador;

public class ParametroContexto extends Contexto {
	protected final Token chave;

	public ParametroContexto(Token chave) {
		this.chave = chave;
	}

	@Context("parametro")
	@Doc("chave")
	@Override
	public void processar(Compilador compilador, Token token) {
		compilador.invalidar(token);
	}
}