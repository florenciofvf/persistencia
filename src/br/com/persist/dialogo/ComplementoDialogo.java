package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.comp.TextField;
import br.com.persist.container.ComplementoContainer;
import br.com.persist.desktop.Objeto;
import br.com.persist.util.IJanela;

public class ComplementoDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ComplementoContainer container;

	public ComplementoDialogo(Dialog dialog, Objeto objeto, TextField txtComplemento) {
		super(dialog, objeto.getId());
		container = new ComplementoContainer(this, objeto, txtComplemento);
		montarLayout();
	}

	public ComplementoDialogo(Frame frame, Objeto objeto, TextField txtComplemento) {
		super(frame, objeto.getId());
		container = new ComplementoContainer(this, objeto, txtComplemento);
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