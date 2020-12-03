package br.com.persist.plugins.objeto.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;

public class Processador {
	private static final Logger LOG = Logger.getGlobal();
	private static Processo processo = new Processo();

	private Processador() {
	}

	public static void processar(Runnable r) {
		try {
			processo.executar(r);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static void inicializar() {
		processo.contador = 0;
	}

	public static void incrementar() {
		processo.incrementar();
	}

	public static void decrementar() {
		processo.decrementar();
	}

	private static class Processo {
		int contador;

		synchronized void incrementar() {
			contador++;
		}

		synchronized void decrementar() {
			contador--;
			notifyAll();
		}

		synchronized void executar(Runnable r) throws InterruptedException {
			while (contador > 0) {
				wait();
			}
			new Thread(r).start();
		}
	}
}