package br.com.persist.icone;

import java.awt.BorderLayout;
import java.awt.Dialog;

import br.com.persist.comp.Label;
import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.objeto.Objeto;
import br.com.persist.util.IJanela;

public class IconeDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final IconeContainer container;

	public IconeDialogo(Dialog dialog, Objeto objeto, Label label) {
		super(dialog, objeto.getId());
		container = new IconeContainer(this, objeto, label);
		setTitle(container.getTotalIcones() + " - " + getTitle());
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