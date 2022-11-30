package br.com.persist.plugins.entrega;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class EntregaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final EntregaContainer container;

	private EntregaDialogo(Frame frame, Formulario formulario) {
		super(frame, EntregaMensagens.getString(EntregaConstantes.LABEL_ENTREGA));
		container = new EntregaContainer(this, formulario, null, null);
		container.setEntregaDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		EntregaDialogo form = new EntregaDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setEntregaDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}