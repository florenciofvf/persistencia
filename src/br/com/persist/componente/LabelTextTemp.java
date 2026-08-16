package br.com.persist.componente;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;

public class LabelTextTemp extends JLabel implements Runnable {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;

	public void mensagemChave(String chave) {
		mensagem(Mensagens.getString(chave));
	}

	public void mensagem(String string) {
		setText(string);
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			Thread.sleep(Preferencias.getSegundosMensagem() * 1000L);
		} catch (InterruptedException e) {
			LOG.log(Level.FINEST, "run()", e);
			Thread.currentThread().interrupt();
		}
		setText(Constantes.VAZIO);
	}
}