package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.container.ComparacaoContainer;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ComparacaoDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ComparacaoContainer container;

	public ComparacaoDialogo(Dialog dialog) {
		super(dialog, Mensagens.getString(Constantes.LABEL_COMPARACAO));
		container = new ComparacaoContainer(this);
		montarLayout();
	}

	public ComparacaoDialogo(Frame frame) {
		super(frame, Mensagens.getString(Constantes.LABEL_COMPARACAO));
		container = new ComparacaoContainer(this);
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