package br.com.persist.plugins.execucao;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class ExecucaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ExecucaoContainer container;

	private ExecucaoDialogo(Frame frame, Formulario formulario) {
		super(frame, ExecucaoMensagens.getString(ExecucaoConstantes.LABEL_EXECUCOES));
		container = new ExecucaoContainer(this, formulario);
		container.setExecucaoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		ExecucaoDialogo form = new ExecucaoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setExecucaoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}