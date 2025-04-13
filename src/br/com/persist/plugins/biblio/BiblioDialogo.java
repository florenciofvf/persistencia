package br.com.persist.plugins.biblio;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class BiblioDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final BiblioContainer container;

	private BiblioDialogo(Frame frame, Formulario formulario) {
		super(frame, BiblioMensagens.getString(BiblioConstantes.LABEL_BIBLIO));
		container = new BiblioContainer(this, formulario);
		container.setBiblioDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		BiblioDialogo form = new BiblioDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setBiblioDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}