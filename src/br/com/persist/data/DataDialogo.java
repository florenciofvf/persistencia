package br.com.persist.data;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;

public class DataDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final DataContainer container;

	private DataDialogo(Dialog dialog, DataListener listener) {
		super(dialog, listener.getTitle());
		container = new DataContainer(this, listener);
		montarLayout();
	}

	private DataDialogo(Frame frame, DataListener listener) {
		super(frame, listener.getTitle());
		container = new DataContainer(this, listener);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static DataDialogo criar(Dialog dialog, DataListener listener) {
		return new DataDialogo(dialog, listener);
	}

	public static DataDialogo criar(Frame frame, DataListener listener) {
		return new DataDialogo(frame, listener);
	}
}