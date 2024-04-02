package br.com.persist.plugins.robo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class RoboDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final RoboContainer container;

	private RoboDialogo(Frame frame, Formulario formulario) {
		super(frame, RoboMensagens.getString(RoboConstantes.LABEL_ROBO));
		container = new RoboContainer(this, formulario, null, null);
		container.setRoboDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		RoboDialogo form = new RoboDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setRoboDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}