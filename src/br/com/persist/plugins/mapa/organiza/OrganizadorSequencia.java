package br.com.persist.plugins.mapa.organiza;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.plugins.mapa.Objeto;

/*
 * organizadorParametros="totalObjetos distancia"
 */
public class OrganizadorSequencia extends Organizador {
	private int distancia;
	private int posicaoX;
	private int total;

	public OrganizadorSequencia() {
		super("sequencia");
		reiniciar();
	}

	@Override
	public void parametros(String string) throws ArgumentoException {
		try {
			String[] strings = string.split(" ");
			total = Integer.parseInt(strings[0]);
			total /= 2;
			distancia = Integer.parseInt(strings[1]);
			posicaoX = total * distancia;
		} catch (Exception e) {
			throw new ArgumentoException("O organizador sequencial necessita do parametro total e distancia.");
		}
	}

	@Override
	public void organizar(Objeto objeto) {
		objeto.getVetor().setX(posicaoX);
		posicaoX -= distancia;
	}

	@Override
	public void reiniciar() {
		posicaoX = total * distancia;
	}
}