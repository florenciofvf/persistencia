package br.com.persist.complemento;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.componente.TextField;
import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.objeto.Objeto;
import br.com.persist.util.IJanela;

public class ComplementoDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ComplementoContainer container;

	public ComplementoDialogo(Dialog dialog, Objeto objeto, TextField txtComplemento, ComplementoListener listener) {
		super(dialog, objeto.getId());
		container = new ComplementoContainer(this, objeto, txtComplemento, listener);
		montarLayout();
	}

	public ComplementoDialogo(Frame frame, Objeto objeto, TextField txtComplemento, ComplementoListener listener) {
		super(frame, objeto.getId());
		container = new ComplementoContainer(this, objeto, txtComplemento, listener);
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