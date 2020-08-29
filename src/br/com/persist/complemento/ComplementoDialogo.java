package br.com.persist.complemento;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.componente.TextField;
import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.objeto.Objeto;

public class ComplementoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ComplementoContainer container;

	private ComplementoDialogo(Dialog dialog, Objeto objeto, TextField txtComplemento, ComplementoListener listener) {
		super(dialog, objeto.getId());
		container = new ComplementoContainer(this, objeto, txtComplemento, listener);
		montarLayout();
	}

	private ComplementoDialogo(Frame frame, Objeto objeto, TextField txtComplemento, ComplementoListener listener) {
		super(frame, objeto.getId());
		container = new ComplementoContainer(this, objeto, txtComplemento, listener);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static ComplementoDialogo criar(Dialog dialog, Objeto objeto, TextField txtComplemento,
			ComplementoListener listener) {
		return new ComplementoDialogo(dialog, objeto, txtComplemento, listener);
	}

	public static ComplementoDialogo criar(Frame frame, Objeto objeto, TextField txtComplemento,
			ComplementoListener listener) {
		return new ComplementoDialogo(frame, objeto, txtComplemento, listener);
	}
}