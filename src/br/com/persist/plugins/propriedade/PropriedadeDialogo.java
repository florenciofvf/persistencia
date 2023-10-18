package br.com.persist.plugins.propriedade;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class PropriedadeDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final PropriedadeContainer container;

	private PropriedadeDialogo(Frame frame, Formulario formulario) {
		super(frame, PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE));
		container = new PropriedadeContainer(this, formulario);
		container.setPropriedadeDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		PropriedadeDialogo form = new PropriedadeDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setPropriedadeDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}