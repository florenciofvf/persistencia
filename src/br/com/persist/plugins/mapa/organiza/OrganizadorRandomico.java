package br.com.persist.plugins.mapa.organiza;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.plugins.mapa.MapaException;
import br.com.persist.plugins.mapa.Objeto;

public class OrganizadorRandomico extends Organizador {
	private static final Logger LOG = Logger.getGlobal();
	private Random random = new Random();

	public OrganizadorRandomico() {
		super("randomico");
	}

	@Override
	public void parametros(String string) throws MapaException {
		throw new MapaException("erro.nao_aceita_param");
	}

	@Override
	public void organizar(Objeto objeto) {
		pausar();
		objeto.getVetor().rotacaoX(random.nextInt(360));
		pausar();
		objeto.getVetor().rotacaoY(random.nextInt(360));
		pausar();
		objeto.getVetor().rotacaoZ(random.nextInt(360));
	}

	private void pausar() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			LOG.log(Level.SEVERE, Constantes.INFO, e);
			Thread.currentThread().interrupt();
		}
	}
}