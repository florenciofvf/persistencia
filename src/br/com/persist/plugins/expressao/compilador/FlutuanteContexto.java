package br.com.persist.plugins.expressao.compilador;

public class FlutuanteContexto extends Contexto {
	public FlutuanteContexto(Token token) {
		this.token = token;
	}

	@Context("flutuante")
	@Doc("123.123")
	@Override
	public void processar(Compilador compilador, Token token) {
		compilador.invalidar(token);
	}
}