package br.com.persist.plugins.objeto.config;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoSuperficie;

public class ObjetoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer container;

	private ObjetoDialogo(Frame frame, ObjetoSuperficie objetoSuperficie, Objeto objeto) {
		super(frame, objeto.getId());
		setSize(Constantes.SIZE2);
		container = new ObjetoContainer(this, objetoSuperficie, objeto);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static ObjetoDialogo criar(Frame frame, ObjetoSuperficie objetoSuperficie, Objeto objeto) {
		ObjetoDialogo form = new ObjetoDialogo(frame, objetoSuperficie, objeto);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		return form;
	}

	@Override
	public void executarAoAbrirDialogo() {
		container.dialogoVisivel();
	}
}