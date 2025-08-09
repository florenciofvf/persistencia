package br.com.persist.plugins.navegacao;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class NavegacaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final NavegacaoContainer container;

	private NavegacaoDialogo(Frame frame, Formulario formulario) {
		super(frame, NavegacaoMensagens.getString(NavegacaoConstantes.LABEL_NAVEGACAO));
		container = new NavegacaoContainer(this, formulario);
		container.setNavegacaoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		NavegacaoDialogo form = new NavegacaoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setNavegacaoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}