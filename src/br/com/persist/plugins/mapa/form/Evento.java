package br.com.persist.plugins.mapa.form;

import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.plugins.mapa.config.Config;

public class Evento {
	private static final Logger LOG = Logger.getGlobal();
	private THREAD thread;
	private Click click;

	public void click(MouseEvent evento) {
		click = new Click(evento);
		thread = new THREAD(click, evento.getClickCount() > 1);
		thread.start();
	}

	public THREAD get() {
		return thread;
	}

	public class THREAD extends Thread {
		final boolean direto;
		boolean avaliado;
		Click click;

		public THREAD(Click click, boolean direto) {
			this.direto = direto;
			this.click = click;
		}

		@Override
		public void run() {
			if (direto) {
				return;
			}
			synchronized (this) {
				try {
					wait(Config.INTERVALO_DUPLO_CLICK);
				} catch (Exception e) {
					LOG.log(Level.SEVERE, Constantes.INFO, e);
				}
				if (this.click != Evento.this.click) {
					this.click = null;
				}
			}
			avaliado = true;
		}

		public Click getClick() {
			if (direto) {
				return click;
			}
			synchronized (this) {
				while (!avaliado) {
					try {
						wait(Config.INTERVALO_DUPLO_CLICK);
					} catch (Exception e) {
						LOG.log(Level.SEVERE, Constantes.INFO, e);
					}
				}
			}
			return click;
		}
	}
}
