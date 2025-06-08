package br.com.persist.plugins.objeto;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.HoraUtil;

public class ThreadManager implements Runnable {
	private static final Logger LOG = Logger.getGlobal();
	final ObjetoSuperficie superficie;
	boolean processar;
	int totalHoras;
	Thread thread;

	ThreadManager(ObjetoSuperficie superficie) {
		this.superficie = superficie;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			processarHora();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void processarHora() {
		totalHoras = 0;
		for (Relacao relacao : superficie.getRelacoes()) {
			try {
				if (HoraUtil.formatoValido(relacao.getDescricao())) {
					totalHoras += HoraUtil.getSegundos(relacao.getDescricao());
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}
		superficie.repaint();
	}

	public void reiniciarHoras() throws AssistenciaException {
		for (Relacao relacao : superficie.getRelacoes()) {
			relacao.reiniciarHoras(true, superficie);
		}
		totalHoras = 0;
		superficie.repaint();
	}

	public void somarHoras(boolean b) {
		if (b) {
			processar = true;
			ativar();
		} else {
			desativar();
		}
		superficie.repaint();
	}

	public void ativar() {
		if (processar && thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void desativar() {
		if (thread != null) {
			thread.interrupt();
			processar = false;
			thread = null;
		}
	}

	public boolean isProcessando() {
		return thread != null;
	}

	public void setProcessar(boolean processar) {
		this.processar = processar;
	}

	boolean validoDesenhar() {
		return processar && thread != null;
	}
}