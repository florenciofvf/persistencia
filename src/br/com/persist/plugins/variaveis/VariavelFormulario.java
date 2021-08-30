package br.com.persist.plugins.variaveis;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.formulario.Formulario;

public class VariavelFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final VariavelContainer container;

	private VariavelFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_VARIAVEIS));
		container = new VariavelContainer(this, formulario);
		container.setVariavelFormulario(this);
		montarLayout();
	}

	private VariavelFormulario(VariavelContainer container) {
		super(Mensagens.getString(Constantes.LABEL_VARIAVEIS));
		container.setVariavelFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, VariavelContainer container) {
		VariavelFormulario form = new VariavelFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		VariavelFormulario form = new VariavelFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setVariavelFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}