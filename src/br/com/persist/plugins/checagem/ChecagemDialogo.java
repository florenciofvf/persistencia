package br.com.persist.plugins.checagem;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class ChecagemDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ChecagemContainer container;

	private ChecagemDialogo(Frame frame, Formulario formulario) {
		super(frame, ChecagemMensagens.getString(ChecagemConstantes.LABEL_CHECAGEM));
		container = new ChecagemContainer(this, formulario, null, null);
		container.setChecagemDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		ChecagemDialogo form = new ChecagemDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setChecagemDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}