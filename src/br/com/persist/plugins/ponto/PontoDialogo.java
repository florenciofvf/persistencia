package br.com.persist.plugins.ponto;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class PontoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final PontoContainer container;

	private PontoDialogo(Frame frame, Formulario formulario) {
		super(frame, PontoMensagens.getString(PontoConstantes.LABEL_PONTO));
		container = new PontoContainer(this, formulario);
		container.setPontoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		PontoDialogo form = new PontoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setPontoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}