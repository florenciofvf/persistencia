package br.com.persist.plugins.ponto;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class PontoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final PontoContainer container;

	private PontoFormulario(Formulario formulario) {
		super(formulario, PontoMensagens.getString(PontoConstantes.LABEL_PONTO));
		container = new PontoContainer(this, formulario);
		container.setPontoFormulario(this);
		montarLayout();
	}

	private PontoFormulario(PontoContainer container) {
		super(container.getFormulario(), PontoMensagens.getString(PontoConstantes.LABEL_PONTO));
		container.setPontoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, PontoContainer container) {
		PontoFormulario form = new PontoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		PontoFormulario form = new PontoFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setPontoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}