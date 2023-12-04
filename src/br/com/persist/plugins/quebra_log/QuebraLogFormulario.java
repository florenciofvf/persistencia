package br.com.persist.plugins.quebra_log;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class QuebraLogFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final QuebraLogContainer container;

	private QuebraLogFormulario(Formulario formulario) {
		super(formulario, QuebraLogMensagens.getString(QuebraLogConstantes.LABEL_QUEBRA_LOG));
		container = new QuebraLogContainer(this, formulario);
		container.setQuebraLogFormulario(this);
		montarLayout();
	}

	private QuebraLogFormulario(QuebraLogContainer container) {
		super(container.getFormulario(), QuebraLogMensagens.getString(QuebraLogConstantes.LABEL_QUEBRA_LOG));
		container.setQuebraLogFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, QuebraLogContainer container) {
		QuebraLogFormulario form = new QuebraLogFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		QuebraLogFormulario form = new QuebraLogFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setQuebraLogFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}
