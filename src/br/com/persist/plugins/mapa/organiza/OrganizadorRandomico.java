package br.com.persist.plugins.mapa.organiza;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.plugins.mapa.forma.Forma;

/*
 * 
 * organizador="randomico"
 * 
 */
public class OrganizadorRandomico implements Organizador {
	private static final Logger LOG = Logger.getGlobal();
	private Random random = new Random();

	@Override
	public void parametros(String string) {
		throw new IllegalStateException();
	}

	@Override
	public void organizar(Forma forma) {
		pausar();
		forma.getVetor().rotacaoX(random.nextInt(360));
		pausar();
		forma.getVetor().rotacaoY(random.nextInt(360));
		pausar();
		forma.getVetor().rotacaoZ(random.nextInt(360));
	}

	private void pausar() {
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.INFO, e);
		}
	}

	@Override
	public void reiniciar() {
		LOG.log(Level.INFO, Constantes.INFO);
	}
}