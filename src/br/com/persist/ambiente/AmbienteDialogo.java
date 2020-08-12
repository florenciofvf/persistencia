package br.com.persist.ambiente;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.IJanela;

public class AmbienteDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final AmbienteContainer container;

	public AmbienteDialogo(Frame frame, Formulario formulario, AmbienteContainer.Ambiente ambiente) {
		super(frame, ambiente.getDescricao());
		container = new AmbienteContainer(this, formulario, null, ambiente);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario, AmbienteContainer.Ambiente ambiente) {
		AmbienteDialogo form = new AmbienteDialogo(formulario, formulario, ambiente);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}