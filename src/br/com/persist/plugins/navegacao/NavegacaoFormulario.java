package br.com.persist.plugins.navegacao;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class NavegacaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final NavegacaoContainer container;

	private NavegacaoFormulario(Formulario formulario) {
		super(formulario, NavegacaoMensagens.getString(NavegacaoConstantes.LABEL_NAVEGACAO));
		container = new NavegacaoContainer(this, formulario);
		container.setNavegacaoFormulario(this);
		montarLayout();
	}

	private NavegacaoFormulario(NavegacaoContainer container) {
		super(container.getFormulario(), NavegacaoMensagens.getString(NavegacaoConstantes.LABEL_NAVEGACAO));
		container.setNavegacaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, NavegacaoContainer container) {
		NavegacaoFormulario form = new NavegacaoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		NavegacaoFormulario form = new NavegacaoFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setNavegacaoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}