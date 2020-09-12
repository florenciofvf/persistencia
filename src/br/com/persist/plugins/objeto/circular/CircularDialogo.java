package br.com.persist.plugins.objeto.circular;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.plugins.objeto.ObjetoSuperficie;
import br.com.persist.plugins.objeto.circular.CircularContainer.Tipo;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class CircularDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final CircularContainer container;

	private CircularDialogo(Frame frame, ObjetoSuperficie objetoSuperficie, Tipo tipo) {
		super(frame, Mensagens.getString(Constantes.LABEL_CIRCULAR));
		container = new CircularContainer(this, objetoSuperficie, tipo);
		montarLayout();
		pack();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Frame frame, ObjetoSuperficie objetoSuperficie, Tipo tipo) {
		CircularDialogo form = new CircularDialogo(frame, objetoSuperficie, tipo);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
	}
}