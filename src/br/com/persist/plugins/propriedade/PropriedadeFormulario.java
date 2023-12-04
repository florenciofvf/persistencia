package br.com.persist.plugins.propriedade;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class PropriedadeFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final PropriedadeContainer container;

	private PropriedadeFormulario(Formulario formulario) {
		super(formulario, PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE));
		container = new PropriedadeContainer(this, formulario);
		container.setPropriedadeFormulario(this);
		montarLayout();
	}

	private PropriedadeFormulario(PropriedadeContainer container) {
		super(container.getFormulario(), PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE));
		container.setPropriedadeFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, PropriedadeContainer container) {
		PropriedadeFormulario form = new PropriedadeFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		PropriedadeFormulario form = new PropriedadeFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setPropriedadeFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}