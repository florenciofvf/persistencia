package br.com.persist.plugins.mapa.organiza;

import br.com.persist.plugins.mapa.Forma;

/*
 * 
 * organizador="circular" organizadorParametros="grau"
 * 
 */
public class OrganizadorCircular implements Organizador {
	private int grau = 15;
	private int soma;

	public OrganizadorCircular() {
		reiniciar();
	}

	@Override
	public void parametros(String string) {
		grau = Integer.parseInt(string);
	}

	@Override
	public void organizar(Forma forma) {
		forma.getVetor().rotacaoZ(soma);
		soma += grau;
	}

	@Override
	public void reiniciar() {
		soma = 0;
	}
}