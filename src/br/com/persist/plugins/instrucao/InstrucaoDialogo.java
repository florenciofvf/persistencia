package br.com.persist.plugins.instrucao;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class InstrucaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final InstrucaoContainer container;

	private InstrucaoDialogo(Frame frame, Formulario formulario) {
		super(frame, InstrucaoMensagens.getString(InstrucaoConstantes.LABEL_INSTRUCAO));
		container = new InstrucaoContainer(this, formulario);
		container.setInstrucaoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		InstrucaoDialogo form = new InstrucaoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setInstrucaoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}