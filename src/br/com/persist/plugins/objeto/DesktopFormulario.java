package br.com.persist.plugins.objeto;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.componente.ScrollPane;
import br.com.persist.formulario.Formulario;

public class DesktopFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final Desktop desktop;

	private DesktopFormulario() {
		super(Mensagens.getString(Constantes.LABEL_FORMULARIO));
		desktop = new Desktop(false);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(desktop));
	}

	public Desktop getDesktop() {
		return desktop;
	}

	public static DesktopFormulario criar(Formulario formulario) {
		DesktopFormulario form = new DesktopFormulario();
		Formulario.posicionarJanela(formulario, form);
		return form;
	}

	@Override
	public void executarAoAbrirFormulario() {
		desktop.executarAoAbrirFormulario();
	}
}