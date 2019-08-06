package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.container.CircularContainer;
import br.com.persist.container.CircularContainer.Tipo;
import br.com.persist.desktop.Objeto;
import br.com.persist.desktop.Superficie;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class CircularDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final CircularContainer container;

	public CircularDialogo(Frame frame, Superficie superficie, Tipo tipo, Objeto pivo) {
		super(frame, Mensagens.getString(Constantes.LABEL_CIRCULAR));
		container = new CircularContainer(this, superficie, tipo, pivo);
		montarLayout();
		pack();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}
}