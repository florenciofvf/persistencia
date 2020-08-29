package br.com.persist.ambiente;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.principal.Formulario;

public class AmbienteDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final AmbienteContainer container;

	private AmbienteDialogo(Frame frame, Formulario formulario, AmbienteContainer.Ambiente ambiente) {
		super(frame, ambiente.getDescricao());
		container = new AmbienteContainer(this, formulario, null, ambiente);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, AmbienteContainer.Ambiente ambiente) {
		AmbienteDialogo form = new AmbienteDialogo(formulario, formulario, ambiente);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}