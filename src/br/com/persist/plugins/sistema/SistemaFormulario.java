package br.com.persist.plugins.sistema;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class SistemaFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final SistemaContainer container;

	private SistemaFormulario(Formulario formulario) {
		super(formulario, SistemaMensagens.getString(SistemaConstantes.LABEL_SISTEMA));
		container = new SistemaContainer(this, formulario);
		container.setSistemaFormulario(this);
		montarLayout();
	}

	private SistemaFormulario(SistemaContainer container) {
		super(container.getFormulario(), SistemaMensagens.getString(SistemaConstantes.LABEL_SISTEMA));
		container.setSistemaFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, SistemaContainer container) {
		SistemaFormulario form = new SistemaFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		SistemaFormulario form = new SistemaFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setSistemaFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}