package br.com.persist.circular;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.circular.CircularContainer.Tipo;
import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.superficie.Superficie;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class CircularDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final CircularContainer container;

	private CircularDialogo(Frame frame, Superficie superficie, Tipo tipo) {
		super(frame, Mensagens.getString(Constantes.LABEL_CIRCULAR));
		container = new CircularContainer(this, superficie, tipo);
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

	public static void criar(Frame frame, Superficie superficie, Tipo tipo) {
		CircularDialogo form = new CircularDialogo(frame, superficie, tipo);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
	}
}