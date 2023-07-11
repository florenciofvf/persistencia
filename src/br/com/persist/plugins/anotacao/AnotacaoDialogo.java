package br.com.persist.plugins.anotacao;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class AnotacaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final AnotacaoContainer container;

	private AnotacaoDialogo(Frame frame, Formulario formulario) {
		super(frame, AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES));
		container = new AnotacaoContainer(this, formulario);
		container.setAnotacaoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		AnotacaoDialogo form = new AnotacaoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setAnotacaoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}