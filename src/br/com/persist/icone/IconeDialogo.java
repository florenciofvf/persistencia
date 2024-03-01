package br.com.persist.icone;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;

public class IconeDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final IconeContainer container;

	private IconeDialogo(String titulo, IconeListener listener, String icone) {
		super((Dialog) null, titulo);
		container = new IconeContainer(this, listener, icone);
		setTitle(container.getTotalIcones() + " - " + getTitle());
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Component c, String titulo, IconeListener listener, String icone) {
		IconeDialogo dialog = new IconeDialogo(titulo, listener, icone);
		dialog.setLocationRelativeTo(Util.getViewParent(c));
		dialog.setVisible(true);
	}
}