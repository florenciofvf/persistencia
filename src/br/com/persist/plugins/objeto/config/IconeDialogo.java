package br.com.persist.plugins.objeto.config;

import java.awt.BorderLayout;
import java.awt.Dialog;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.componente.Label;
import br.com.persist.plugins.objeto.Objeto;

public class IconeDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final IconeContainer container;

	private IconeDialogo(Dialog dialog, Objeto objeto, Label label) {
		super(dialog, objeto.getId());
		container = new IconeContainer(this, objeto, label);
		setTitle(container.getTotalIcones() + " - " + getTitle());
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static IconeDialogo criar(Dialog dialog, Objeto objeto, Label label) {
		return new IconeDialogo(dialog, objeto, label);
	}
}