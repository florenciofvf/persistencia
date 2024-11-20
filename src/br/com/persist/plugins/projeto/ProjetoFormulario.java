package br.com.persist.plugins.projeto;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class ProjetoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ProjetoContainer container;

	private ProjetoFormulario(Formulario formulario) {
		super(formulario, ProjetoMensagens.getString(ProjetoConstantes.LABEL_PROJETO));
		container = new ProjetoContainer(this, formulario);
		container.setProjetoFormulario(this);
		montarLayout();
	}

	private ProjetoFormulario(ProjetoContainer container) {
		super(container.getFormulario(), ProjetoMensagens.getString(ProjetoConstantes.LABEL_PROJETO));
		container.setProjetoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, ProjetoContainer container) {
		ProjetoFormulario form = new ProjetoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		ProjetoFormulario form = new ProjetoFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setProjetoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}