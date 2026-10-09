package br.com.persist.plugins.labirinto;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class LabirintoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final LabirintoContainer container;

	private LabirintoDialogo(Frame frame, Formulario formulario) {
		super(frame, LabirintoMensagens.getString(LabirintoConstantes.LABEL_LABIRINTO));
		container = new LabirintoContainer(this, formulario, null, null);
		container.setLabirintoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		LabirintoDialogo form = new LabirintoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setLabirintoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}