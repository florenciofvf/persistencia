package br.com.persist.plugins.mapa.organiza;

import br.com.persist.plugins.mapa.Objeto;

/*
 * organizadorParametros="grau"
 */
public class OrganizadorCircular extends Organizador {
	private int grau = 15;
	private int soma;

	public OrganizadorCircular() {
		super("circular");
		reiniciar();
	}

	@Override
	public void parametros(String string) {
		grau = Integer.parseInt(string);
	}

	@Override
	public void organizar(Objeto objeto) {
		objeto.getVetor().rotacaoZ(soma);
		soma += grau;
	}

	@Override
	public void reiniciar() {
		soma = 0;
	}
}