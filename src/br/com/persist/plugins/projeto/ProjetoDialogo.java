package br.com.persist.plugins.projeto;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class ProjetoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ProjetoContainer container;

	private ProjetoDialogo(Frame frame, Formulario formulario) {
		super(frame, ProjetoMensagens.getString(ProjetoConstantes.LABEL_PROJETO));
		container = new ProjetoContainer(this, formulario);
		container.setProjetoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		ProjetoDialogo form = new ProjetoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setProjetoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}