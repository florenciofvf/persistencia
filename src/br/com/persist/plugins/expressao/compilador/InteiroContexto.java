package br.com.persist.plugins.expressao.compilador;

public class InteiroContexto extends Contexto {
	public InteiroContexto(Token token) {
		this.token = token;
	}

	@Context("inteiro")
	@Doc("123")
	@Override
	public void processar(Compilador compilador, Token token) {
		compilador.invalidar(token);
	}
}