package br.com.persist.plugins.expressao.compilador;

public class InvocacaoContexto extends Contexto {
	protected final Token operador;

	public InvocacaoContexto(Token operador) {
		this.operador = operador;
	}

	@Context("operador")
	@Override
	public void processar(Compilador compilador, Token token) {
		compilador.invalidar(token);
	}
}