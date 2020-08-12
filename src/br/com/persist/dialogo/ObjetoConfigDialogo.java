package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.objeto.Objeto;
import br.com.persist.objeto.ObjetoConfigContainer;
import br.com.persist.superficie.Superficie;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;

public class ObjetoConfigDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ObjetoConfigContainer container;

	public ObjetoConfigDialogo(Frame frame, Superficie superficie, Objeto objeto) {
		super(frame, objeto.getId());
		setSize(Constantes.SIZE2);
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