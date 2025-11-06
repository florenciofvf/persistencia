package br.com.persist.plugins.ouvinte;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class OuvinteDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final OuvinteContainer container;

	private OuvinteDialogo(Frame frame, Formulario formulario) {
		super(frame, OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE));
		container = new OuvinteContainer(this, formulario);
		container.setOuvinteDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		OuvinteDialogo form = new OuvinteDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setOuvinteDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}