package br.com.persist.plugins.objeto.complem;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;

public class ComplementoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ComplementoContainer container;

	private ComplementoDialogo(Dialog dialog, ComplementoListener listener) {
		super(dialog, listener.getTitle());
		container = new ComplementoContainer(this, listener);
		montarLayout();
	}

	private ComplementoDialogo(Frame frame, ComplementoListener listener) {
		super(frame, listener.getTitle());
		container = new ComplementoContainer(this, listener);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static ComplementoDialogo criar(Dialog dialog, ComplementoListener listener) {
		return new ComplementoDialogo(dialog, listener);
	}

	public static ComplementoDialogo criar(Frame frame, ComplementoListener listener) {
		return new ComplementoDialogo(frame, listener);
	}
}