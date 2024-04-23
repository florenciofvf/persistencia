package br.com.persist.plugins.objeto.vinculo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;

public class OrdenarDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final OrdenarContainer container;

	private OrdenarDialogo(String titulo, OrdenarListener listener) {
		super((Dialog) null, titulo);
		container = new OrdenarContainer(this, listener);
		setTitle(listener.getPesquisas().size() + " - " + getTitle());
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Component c, String titulo, OrdenarListener listener) {
		OrdenarDialogo dialog = new OrdenarDialogo(titulo, listener);
		dialog.pack();
		dialog.setLocationRelativeTo(Util.getViewParent(c));
		dialog.setVisible(true);
	}
}