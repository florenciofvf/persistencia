package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.container.ComparacaoContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ComparacaoDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ComparacaoContainer container;

	public ComparacaoDialogo(Dialog dialog, Formulario formulario) {
		super(dialog, Mensagens.getString(Constantes.LABEL_COMPARACAO));
		container = new ComparacaoContainer(this, formulario);
		montarLayout();
	}

	public ComparacaoDialogo(Frame frame, Formulario formulario) {
		super(frame, Mensagens.getString(Constantes.LABEL_COMPARACAO));
		container = new ComparacaoContainer(this, formulario);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario) {
		ComparacaoDialogo form = new ComparacaoDialogo(formulario, formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}