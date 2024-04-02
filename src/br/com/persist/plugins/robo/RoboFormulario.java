package br.com.persist.plugins.robo;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class RoboFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final RoboContainer container;

	private RoboFormulario(Formulario formulario, String conteudo, String idPagina) {
		super(formulario, RoboMensagens.getString(RoboConstantes.LABEL_ROBO));
		container = new RoboContainer(this, formulario, conteudo, idPagina);
		container.setRoboFormulario(this);
		montarLayout();
	}

	private RoboFormulario(RoboContainer container) {
		super(container.getFormulario(), RoboMensagens.getString(RoboConstantes.LABEL_ROBO));
		container.setRoboFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, RoboContainer container) {
		RoboFormulario form = new RoboFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario, String conteudo, String idPagina) {
		RoboFormulario form = new RoboFormulario(formulario, conteudo, idPagina);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setRoboFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}