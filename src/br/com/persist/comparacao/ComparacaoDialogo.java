package br.com.persist.comparacao;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class ComparacaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ComparacaoContainer container;

	private ComparacaoDialogo(Dialog dialog, Formulario formulario) {
		super(dialog, Mensagens.getString(Constantes.LABEL_COMPARACAO));
		container = new ComparacaoContainer(this, formulario);
		montarLayout();
	}

	private ComparacaoDialogo(Frame frame, Formulario formulario) {
		super(frame, Mensagens.getString(Constantes.LABEL_COMPARACAO));
		container = new ComparacaoContainer(this, formulario);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		criar(formulario, formulario);
	}

	public static void criar(Dialog dialog, Formulario formulario) {
		ComparacaoDialogo form = new ComparacaoDialogo(dialog, formulario);
		form.setLocationRelativeTo(dialog);
		form.setVisible(true);
	}

	public static void criar(Frame frame, Formulario formulario) {
		ComparacaoDialogo form = new ComparacaoDialogo(frame, formulario);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
	}
}