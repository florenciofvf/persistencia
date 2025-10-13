package br.com.persist.plugins.sistema;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class SistemaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final SistemaContainer container;

	private SistemaDialogo(Frame frame, Formulario formulario) {
		super(frame, SistemaMensagens.getString(SistemaConstantes.LABEL_SISTEMA));
		container = new SistemaContainer(this, formulario);
		container.setSistemaDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		SistemaDialogo form = new SistemaDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setSistemaDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}