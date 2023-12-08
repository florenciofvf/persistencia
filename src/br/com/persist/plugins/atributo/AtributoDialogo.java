package br.com.persist.plugins.atributo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class AtributoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final AtributoContainer container;

	private AtributoDialogo(Frame frame, Formulario formulario) {
		super(frame, AtributoMensagens.getString(AtributoConstantes.LABEL_ATRIBUTO));
		container = new AtributoContainer(this, formulario, null, null);
		container.setAtributoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		AtributoDialogo form = new AtributoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setAtributoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}