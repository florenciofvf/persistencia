package br.com.persist.plugins.biblio;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class BiblioFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final BiblioContainer container;

	private BiblioFormulario(Formulario formulario) {
		super(formulario, BiblioMensagens.getString(BiblioConstantes.LABEL_BIBLIO));
		container = new BiblioContainer(this, formulario);
		container.setBiblioFormulario(this);
		montarLayout();
	}

	private BiblioFormulario(BiblioContainer container) {
		super(container.getFormulario(), BiblioMensagens.getString(BiblioConstantes.LABEL_BIBLIO));
		container.setBiblioFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, BiblioContainer container) {
		BiblioFormulario form = new BiblioFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		BiblioFormulario form = new BiblioFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setBiblioFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}