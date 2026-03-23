package br.com.persist.plugins.expressao.compilador;

public class ChaveContexto extends Contexto {
	public ChaveContexto(Token token) {
		this.token = token;
	}

	@Context("chave")
	@Doc("chave / chave2")
	@Override
	public void processar(Compilador compilador, Token token) {
		compilador.invalidar(token);
	}
}