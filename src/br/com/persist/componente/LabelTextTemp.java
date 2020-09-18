package br.com.persist.componente;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;

public class LabelTextTemp extends JLabel implements Runnable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getGlobal();
	private int atraso = 500;

	public void mensagemChave(String chave) {
		mensagem(Mensagens.getString(chave));
	}

	public void mensagem(String string) {
		setText(string);
		new Thread(this).start();
	}

	public int getAtraso() {
		return atraso;
	}

	public void setAtraso(int atraso) {
		if (atraso < 0) {
			return;
		}

		this.atraso = atraso;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(atraso);
		} catch (Exception e) {
			LOG.log(Level.FINEST, "run()");
		}

		setText(Constantes.VAZIO);
	}
}