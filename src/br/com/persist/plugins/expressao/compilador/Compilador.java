package br.com.persist.plugins.expressao.compilador;

public class Compilador {
	private Contexto selecionado;

	public void invalidar(Token token, String chaveMsg) {

	}

	public void invalidar(Token token) {

	}

	public void setSelecionado(Contexto selecionado) {
		this.selecionado = selecionado;
	}

	public Contexto getSelecionado() {
		return selecionado;
	}
}