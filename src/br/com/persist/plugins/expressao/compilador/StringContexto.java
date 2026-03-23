package br.com.persist.plugins.expressao.compilador;

public class StringContexto extends Contexto {
	public StringContexto(Token token) {
		this.token = token;
	}

	@Context("string")
	@Doc("'xyz'")
	@Override
	public void processar(Compilador compilador, Token token) {
		compilador.invalidar(token);
	}
}