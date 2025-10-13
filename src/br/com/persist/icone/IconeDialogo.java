package br.com.persist.icone;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;

public class IconeDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final IconeContainer container;

	private IconeDialogo(Dialog dialogo, String titulo, IconeListener listener, String icone) {
		super(dialogo, titulo);
		container = new IconeContainer(this, listener, icone);
		setTitle(container.getTotalIcones() + " - " + getTitle());
		montarLayout();
	}

	private IconeDialogo(Frame frame, String titulo, IconeListener listener, String icone) {
		super(frame, titulo);
		container = new IconeContainer(this, listener, icone);
		setTitle(container.getTotalIcones() + " - " + getTitle());
		montarLayout();
	}

	private IconeDialogo(String titulo, IconeListener listener, String icone) {
		super((Dialog) null, titulo);
		container = new IconeContainer(this, listener, icone);
		setTitle(container.getTotalIcones() + " - " + getTitle());
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static IconeDialogo criar(Dialog dialogo, String titulo, IconeListener listener, String icone) {
		return new IconeDialogo(dialogo, titulo, listener, icone);
	}

	public static IconeDialogo criar(Frame frame, String titulo, IconeListener listener, String icone) {
		return new IconeDialogo(frame, titulo, listener, icone);
	}

	public static IconeDialogo criar(String titulo, IconeListener listener, String icone) {
		return new IconeDialogo(titulo, listener, icone);
	}
}