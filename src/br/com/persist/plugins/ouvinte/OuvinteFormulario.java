package br.com.persist.plugins.ouvinte;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class OuvinteFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final OuvinteContainer container;

	private OuvinteFormulario(Formulario formulario) {
		super(formulario, OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE));
		container = new OuvinteContainer(this, formulario);
		container.setOuvinteFormulario(this);
		montarLayout();
	}

	private OuvinteFormulario(OuvinteContainer container) {
		super(container.getFormulario(), OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE));
		container.setOuvinteFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, OuvinteContainer container) {
		OuvinteFormulario form = new OuvinteFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		OuvinteFormulario form = new OuvinteFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setOuvinteFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}