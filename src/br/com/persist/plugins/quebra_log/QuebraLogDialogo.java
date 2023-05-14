package br.com.persist.plugins.quebra_log;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class QuebraLogDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final QuebraLogContainer container;

	private QuebraLogDialogo(Frame frame, Formulario formulario) {
		super(frame, QuebraLogMensagens.getString(QuebraLogConstantes.LABEL_QUEBRA_LOG));
		container = new QuebraLogContainer(this, formulario);
		container.setQuebraLogDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		QuebraLogDialogo form = new QuebraLogDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setQuebraLogDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}
