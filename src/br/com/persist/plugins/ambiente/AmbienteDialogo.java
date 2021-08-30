package br.com.persist.plugins.ambiente;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class AmbienteDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final AmbienteContainer container;

	private AmbienteDialogo(Frame frame, Formulario formulario, AmbienteContainer.Ambiente ambiente) {
		super(frame, ambiente.getDescricao());
		container = new AmbienteContainer(this, formulario, null, ambiente);
		container.setAmbienteDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, AmbienteContainer.Ambiente ambiente) {
		AmbienteDialogo form = new AmbienteDialogo(formulario, formulario, ambiente);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setAmbienteDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(this);
	}
}