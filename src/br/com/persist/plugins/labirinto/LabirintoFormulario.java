package br.com.persist.plugins.labirinto;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class LabirintoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final LabirintoContainer container;

	private LabirintoFormulario(Formulario formulario, String conteudo, String idPagina) {
		super(formulario, LabirintoMensagens.getString(LabirintoConstantes.LABEL_LABIRINTO));
		container = new LabirintoContainer(this, formulario, conteudo, idPagina);
		container.setLabirintoFormulario(this);
		montarLayout();
	}

	private LabirintoFormulario(LabirintoContainer container) {
		super(container.getFormulario(), LabirintoMensagens.getString(LabirintoConstantes.LABEL_LABIRINTO));
		container.setLabirintoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, LabirintoContainer container) {
		LabirintoFormulario form = new LabirintoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario, String conteudo, String idPagina) {
		LabirintoFormulario form = new LabirintoFormulario(formulario, conteudo, idPagina);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setLabirintoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}