package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.Objeto;
import br.com.persist.container.ObjetoConfigContainer;
import br.com.persist.desktop.Superficie;
import br.com.persist.util.IJanela;

public class ObjetoConfigDialogo extends AbstratoDialogoTMP implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ObjetoConfigContainer container;

	public ObjetoConfigDialogo(Frame frame, Superficie superficie, Objeto objeto) {
		super(frame, objeto.getId());
		container = new ObjetoConfigContainer(this, superficie, objeto);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}
}