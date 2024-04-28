package br.com.persist.plugins.objeto.vinculo;

import java.awt.Component;
import java.awt.Dialog;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;

public class OrdenarArrastoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;

	private OrdenarArrastoDialogo(String titulo, OrdenarListener listener) {
		super((Dialog) null, titulo);
		setTitle(listener.getPesquisas().size() + " - " + getTitle());
		montarLayout();
	}

	private void montarLayout() {
	}

	public static void criar(Component c, String titulo, OrdenarListener listener) {
		OrdenarArrastoDialogo dialog = new OrdenarArrastoDialogo(titulo, listener);
		dialog.pack();
		dialog.setLocationRelativeTo(Util.getViewParent(c));
		dialog.setVisible(true);
	}
}